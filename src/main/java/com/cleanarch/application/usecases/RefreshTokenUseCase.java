package com.cleanarch.application.usecases;

import com.cleanarch.application.port.in.RefreshTokenCommand;
import com.cleanarch.application.port.in.RefreshTokenResult;
import com.cleanarch.application.port.in.RefreshTokenUseCasePort;
import com.cleanarch.application.port.out.SessionRepositoryPort;
import com.cleanarch.application.port.out.TokenGeneratorPort;
import com.cleanarch.application.port.out.TokenHasherPort;
import com.cleanarch.application.port.out.TokenParserPort;
import com.cleanarch.domain.exception.InvalidRefreshTokenException;
import com.cleanarch.domain.exception.RefreshTokenReuseDetectionException;
import com.cleanarch.domain.exception.SecurityBreachException;
import com.cleanarch.domain.model.Session;

import java.util.Optional;
import java.util.UUID;

public class RefreshTokenUseCase implements RefreshTokenUseCasePort {

    private final SessionRepositoryPort sessionRepository;
    private final TokenParserPort tokenParser;
    private final TokenGeneratorPort tokenGenerator;
    private final TokenHasherPort tokenHasher;

    public RefreshTokenUseCase(SessionRepositoryPort sessionRepository, TokenParserPort tokenParser, TokenGeneratorPort tokenGenerator, TokenHasherPort tokenHasher) {
        this.sessionRepository = sessionRepository;
        this.tokenParser = tokenParser;
        this.tokenGenerator = tokenGenerator;
        this.tokenHasher = tokenHasher;
    }

    @Override
    public RefreshTokenResult execute(RefreshTokenCommand command) {

        // Parsing token and fetching session id
        UUID sessionId = tokenParser.extractSessionId(command.refreshToken());

        if(sessionId == null) throw new InvalidRefreshTokenException();

        // Fetching session using session id
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(InvalidRefreshTokenException::new);

        // hash incoming token
        String tokenHash = tokenHasher.hash(command.refreshToken());

        try {

            session.verifyRefreshToken(tokenHash);

        } catch(RefreshTokenReuseDetectionException e)
        {
            sessionRepository.revokeAllByUserId(session.getUserId());
            throw new SecurityBreachException();
        }

        // generate refresh token, hash and save the updated session
        String newRefreshToken = tokenGenerator.generateRefreshToken(session.getUserId());
        String newHash = tokenHasher.hash(newRefreshToken);

        session.rotateRefreshToken(newHash);
        sessionRepository.save(session);

        // generate access token
        String accessToken = tokenGenerator.generateAccessToken(
                session.getUserId(),
                session.getSessionId(),
                "USER"
        );

        return new RefreshTokenResult(
                newRefreshToken,
                accessToken
        );

    }
}
