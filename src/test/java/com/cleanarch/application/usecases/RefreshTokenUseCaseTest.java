package com.cleanarch.application.usecases;

import com.cleanarch.application.port.in.RefreshTokenCommand;
import com.cleanarch.application.port.in.RefreshTokenResult;
import com.cleanarch.application.port.out.SessionRepositoryPort;
import com.cleanarch.application.port.out.TokenGeneratorPort;
import com.cleanarch.application.port.out.TokenHasherPort;
import com.cleanarch.application.port.out.TokenParserPort;
import com.cleanarch.domain.exception.InvalidRefreshTokenException;
import com.cleanarch.domain.exception.RefreshTokenReuseDetectionException;
import com.cleanarch.domain.exception.SecurityBreachException;
import com.cleanarch.domain.exception.SessionExpiredException;
import com.cleanarch.domain.model.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RefreshTokenUseCaseTest {

    private SessionRepositoryPort sessionRepository;
    private TokenParserPort tokenParser;
    private TokenGeneratorPort tokenGenerator;
    private TokenHasherPort tokenHasher;
    private RefreshTokenUseCase refreshTokenUseCase;

    @BeforeEach
    void setup()
    {
        sessionRepository = mock(SessionRepositoryPort.class);
        tokenParser = mock(TokenParserPort.class);
        tokenGenerator = mock(TokenGeneratorPort.class);
        tokenHasher = mock(TokenHasherPort.class);

        refreshTokenUseCase = new RefreshTokenUseCase(
                sessionRepository,
                tokenParser,
                tokenGenerator,
                tokenHasher
        );
    }

    @Test
    public void should_rotate_tokens_when_refresh_token_is_valid()
    {
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Session session = Session.create(
                sessionId,
                userId,
                "hashed-refresh-token",
                Instant.now().plus(30, ChronoUnit.DAYS),
                "device123",
                "0.0.0.1",
                "Chrome"
        );

        RefreshTokenCommand command = new RefreshTokenCommand(
                "refresh-token"
        );

        when(tokenParser.extractSessionId(command.refreshToken()))
                .thenReturn(session.getSessionId());

        when(sessionRepository.findById(session.getSessionId()))
                .thenReturn(Optional.of(session));

        when(tokenHasher.hash(command.refreshToken()))
                .thenReturn("hashed-refresh-token");

        when(tokenGenerator.generateAccessToken(userId, sessionId, "USER"))
                .thenReturn("new-access-token");

        when(tokenGenerator.generateRefreshToken(userId, sessionId))
                .thenReturn("new-refresh-token");

        when(tokenHasher.hash("new-refresh-token"))
                .thenReturn("hashed-new-refresh-token");

        RefreshTokenResult result = refreshTokenUseCase.execute(command);

        assertEquals("new-access-token", result.accessToken());
        assertEquals("new-refresh-token", result.refreshToken());

        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    public void should_throw_invalid_refresh_token_when_session_not_found()
    {
        UUID sessionId = UUID.randomUUID();

        when(tokenParser.extractSessionId("refresh-token"))
                .thenReturn(sessionId);

        when(sessionRepository.findById(sessionId))
                .thenReturn(Optional.empty());

        RefreshTokenCommand command = new RefreshTokenCommand("refresh-token");

        assertThrows(
                InvalidRefreshTokenException.class,
                () -> refreshTokenUseCase.execute(command)
        );

        verify(sessionRepository, never()).save(any());
    }

    @Test
    public void should_throw_reuse_detected_exception()
    {
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Session session = Session.create(
                sessionId,
                userId,
                "hashA",
                Instant.now().plus(30, ChronoUnit.DAYS),
                "device123",
                "0.0.0.1",
                "Chrome"
        );

        session.rotateRefreshToken("hashB");

        RefreshTokenCommand command = new RefreshTokenCommand("tokenA");

        when(tokenParser.extractSessionId("tokenA"))
                .thenReturn(sessionId);

        when(sessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(tokenHasher.hash("tokenA"))
                .thenReturn("hashA");

        assertThrows(
                SecurityBreachException.class,
                () -> refreshTokenUseCase.execute(command)
        );

        verify(sessionRepository).revokeAllByUserId(userId);
    }

    @Test
    public void should_throw_session_expired_when_token_is_expired()
    {
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Session session = Session.create(
                sessionId,
                userId,
                "hashed-token",
                Instant.now(),
                "device123",
                "0.0.0.1",
                "Chrome"
        );

        RefreshTokenCommand command = new RefreshTokenCommand("refresh-token");

        when(tokenParser.extractSessionId(command.refreshToken()))
                .thenReturn(sessionId);

        when(sessionRepository.findById(sessionId))
                .thenReturn(Optional.of(session));

        when(tokenHasher.hash(command.refreshToken()))
                .thenReturn("hashed-token");

        assertThrows(
                SessionExpiredException.class,
                () -> refreshTokenUseCase.execute(command)
        );

        verify(sessionRepository).save(any(Session.class));
    }

}
