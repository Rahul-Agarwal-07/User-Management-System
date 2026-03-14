package com.cleanarch.application.usecases;

import com.cleanarch.application.port.in.LoginUseCasePort;
import com.cleanarch.application.port.in.LoginUserCommand;
import com.cleanarch.application.port.in.LoginUserResult;
import com.cleanarch.application.port.out.*;
import com.cleanarch.domain.exception.InvalidCredentialsException;
import com.cleanarch.domain.model.Session;
import com.cleanarch.domain.model.User;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Transactional
public class LoginUserUseCase implements LoginUseCasePort {

    private static final int MAX_SESSIONS = 3;

    private final UserRepositoryPort userRepository;
    private final SessionRepositoryPort sessionRepository;
    private final TokenGeneratorPort tokenGenerator;
    private final PasswordHasherPort passwordHasher;
    private final TokenHasherPort tokenHasher;

    public LoginUserUseCase(UserRepositoryPort userRepository, SessionRepositoryPort sessionRepository, TokenGeneratorPort tokenGenerator, PasswordHasherPort passwordHasher, TokenHasherPort tokenHasher) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.tokenGenerator = tokenGenerator;
        this.passwordHasher = passwordHasher;
        this.tokenHasher = tokenHasher;
    }

    @Override
    public LoginUserResult execute(LoginUserCommand command) {

        //check if email exists
        User user = userRepository.findByEmail(command.email())
                .orElseThrow( () -> new InvalidCredentialsException("Invalid Email or Password"));

        //check if password matches
        boolean valid = passwordHasher.matches(command.password(), user.getPasswordHash());

        if(!valid) throw new InvalidCredentialsException("Invalid Email or Password");

        // Fetching all the active sessions of user
        List<Session> sessions = sessionRepository.findActiveSessionsByUserId(user.getId());

        // Enforce Session Limit
        if(sessions.size() >= MAX_SESSIONS)
        {
            Session oldest = sessions.stream()
                    .min(Comparator.comparing(Session::getCreatedAt))
                    .orElseThrow();

            oldest.revoke();

            sessionRepository.save(oldest);
        }

        // Generate Refresh Token and Token hash

        UUID sessionId = UUID.randomUUID();

        String refreshToken = tokenGenerator.generateRefreshToken(user.getId(), sessionId);
        String refreshTokenHash = tokenHasher.hash(refreshToken);


        // Create new Session
        Session session = Session.create(
                sessionId,
                user.getId(),
                refreshTokenHash,
                Instant.now().plus(30, ChronoUnit.DAYS),
                command.deviceId(),
                command.ipAddress(),
                command.userAgent()
        );

        sessionRepository.save(session);

        // Generate Access Token
        String accessToken = tokenGenerator.generateAccessToken(
                user.getId(),
                session.getSessionId(),
                "USER"
        );

        return new LoginUserResult(
                accessToken,
                refreshToken,
                user.getId(),
                "USER"
        );
    }
}
