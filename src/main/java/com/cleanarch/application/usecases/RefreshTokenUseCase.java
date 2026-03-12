package com.cleanarch.application.usecases;

import com.cleanarch.application.port.in.RefreshTokenCommand;
import com.cleanarch.application.port.in.RefreshTokenResult;
import com.cleanarch.application.port.in.RefreshTokenUseCasePort;
import com.cleanarch.application.port.out.SessionRepositoryPort;
import com.cleanarch.application.port.out.TokenGeneratorPort;
import com.cleanarch.application.port.out.TokenHasherPort;
import com.cleanarch.application.port.out.TokenParserPort;
import com.cleanarch.domain.exception.*;
import com.cleanarch.domain.model.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.UUID;

public class RefreshTokenUseCase implements RefreshTokenUseCasePort {

    private static final Logger log = LogManager.getLogger(RefreshTokenUseCase.class);
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

        log.debug("Session Id {}", sessionId);

        if (sessionId == null) throw new InvalidRefreshTokenException();

        // Fetching session using session id
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(InvalidRefreshTokenException::new);

        // hash incoming token
        String tokenHash = tokenHasher.hash(command.refreshToken());

        try {

            session.verifyRefreshToken(tokenHash);

        } catch (RefreshTokenReuseDetectionException e) {

            sessionRepository.revokeAllByUserId(session.getUserId());
            throw new SecurityBreachException();

        } catch (SessionRevokedException e) {

            throw new SecurityBreachException(e.getMessage());

        } catch (SessionExpiredException | InvalidRefreshTokenException e)
        {
            sessionRepository.save(session);
            throw e;
        }

        // generate refresh token, hash and save the updated session
        String newRefreshToken = tokenGenerator.generateRefreshToken(session.getUserId(), sessionId);
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
