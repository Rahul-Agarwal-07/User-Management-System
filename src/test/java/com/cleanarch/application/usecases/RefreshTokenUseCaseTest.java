package com.cleanarch.application.usecases;

import com.cleanarch.application.port.in.RefreshTokenCommand;
import com.cleanarch.application.port.out.SessionRepositoryPort;
import com.cleanarch.application.port.out.TokenGeneratorPort;
import com.cleanarch.application.port.out.TokenHasherPort;
import com.cleanarch.application.port.out.TokenParserPort;
import com.cleanarch.domain.model.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        Session session = Session.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
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
                .thenReturn(Optional.of(mock(Session.class)));

        when(tokenHasher.hash(command.refreshToken()))
                .thenReturn("hashed-refresh-token");

        when(session.verifyRefreshToken("hashed-refresh-token"))
                .thenReturn(any());


    }

}
