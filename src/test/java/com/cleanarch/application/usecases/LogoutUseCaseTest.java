package com.cleanarch.application.usecases;

import com.cleanarch.application.port.in.LogoutCommand;
import com.cleanarch.application.port.out.SessionRepositoryPort;
import com.cleanarch.domain.model.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class LogoutUseCaseTest {

    private SessionRepositoryPort sessionRepository;
    private LogoutUseCase logoutUseCase;

    @BeforeEach
    void setup(){
        this.sessionRepository = mock(SessionRepositoryPort.class);
        logoutUseCase = new LogoutUseCase(sessionRepository);
    }

    @Test
    void should_revoke_session_when_logout_success()
    {
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Session session = Session.create(
                sessionId,
                userId,
                "hashed-token",
                Instant.now().plus(30, ChronoUnit.DAYS),
                "device123",
                "0.0.0.1",
                "Chrome"
        );

        when(sessionRepository.findByIdAndUserId(sessionId, userId))
                .thenReturn(Optional.of(session));

        LogoutCommand command = new LogoutCommand(sessionId, userId);

        logoutUseCase.execute(command);

        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void should_do_nothing_when_session_does_not_exist()
    {
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(sessionRepository.findByIdAndUserId(sessionId, userId))
                .thenReturn(Optional.empty());

        LogoutCommand command = new LogoutCommand(sessionId, userId);
        logoutUseCase.execute(command);

        verify(sessionRepository, never()).save(any(Session.class));
    }

    @Test
    void should_do_nothing_when_session_is_already_revoked()
    {
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Session session = Session.create(
                sessionId,
                userId,
                "hashed-token",
                Instant.now().plus(30, ChronoUnit.DAYS),
                "device123",
                "0.0.0.1",
                "Chrome"
        );

        session.revoke();

        when(sessionRepository.findByIdAndUserId(sessionId, userId))
                .thenReturn(Optional.of(session));

        LogoutCommand command = new LogoutCommand(sessionId, userId);
        logoutUseCase.execute(command);

        verify(sessionRepository, never()).save(any(Session.class));
    }
}
