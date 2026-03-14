package com.cleanarch.application.usecases;

import com.cleanarch.application.port.in.LogoutCommand;
import com.cleanarch.application.port.in.LogoutUseCasePort;
import com.cleanarch.application.port.out.SessionRepositoryPort;
import com.cleanarch.domain.model.Session;
import com.cleanarch.domain.model.SessionStatus;
import jakarta.transaction.Transactional;

@Transactional
public class LogoutUseCase implements LogoutUseCasePort {

    private final SessionRepositoryPort sessionRepository;

    public LogoutUseCase(SessionRepositoryPort sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public void execute(LogoutCommand command) {

        var sessionOpt = sessionRepository.findByIdAndUserId(
                command.sessionId(),
                command.userId()
        );

        if(sessionOpt.isEmpty()) return;

        Session session = sessionOpt.get();

        if(session.isRevoked()) return;

        session.revoke();
        sessionRepository.save(session);
    }
}
