package com.cleanarch.domain.model;

import com.cleanarch.domain.exception.InvalidRefreshTokenException;
import com.cleanarch.domain.exception.RefreshTokenReuseDetectionException;
import com.cleanarch.domain.exception.SessionExpiredException;

import java.time.Instant;
import java.util.UUID;

public class Session {

    private UUID sessionId;
    private UUID userId;

    private String currTokenHash;
    private String prevTokenHash;

    private Instant createdAt;
    private Instant lastUsedAt;
    private Instant expiresAt;

    private SessionStatus status;

    private Session() {}

    public static Session create(UUID userId, String refreshTokenHash, Instant expiresAt)
    {
        Session session = new Session();

        session.sessionId = UUID.randomUUID();
        session.userId = userId;

        session.currTokenHash = refreshTokenHash;
        session.prevTokenHash = null;

        session.status = SessionStatus.ACTIVE;

        session.createdAt = Instant.now();
        session.lastUsedAt = Instant.now();
        session.expiresAt = expiresAt;

        return session;
    }

    public void verifyRefreshToken(String tokenHash)
    {
        ensureSessionActive();

        if(isExpired())
        {
            this.status = SessionStatus.EXPIRED;
            throw new SessionExpiredException();
        }

        if(currTokenHash.equals(tokenHash)) return;

        if(prevTokenHash != null && prevTokenHash.equals(tokenHash))
        {
            this.status = SessionStatus.COMPROMISED;
            throw new RefreshTokenReuseDetectionException();
        }

        throw new InvalidRefreshTokenException();
    }

    public void rotateRefreshToken(String tokenHash)
    {
        ensureSessionActive();

        prevTokenHash = currTokenHash;
        currTokenHash = tokenHash;
        lastUsedAt = Instant.now();
    }

    public void revoke()
    {
        if(status != SessionStatus.ACTIVE) return;

        this.status = SessionStatus.REVOKED;
        this.lastUsedAt = Instant.now();
    }

    private void ensureSessionActive()
    {
        if(status == SessionStatus.REVOKED)
            throw new SessionExpiredException();

        if(status == SessionStatus.COMPROMISED)
            throw new RefreshTokenReuseDetectionException();
    }

    private boolean isExpired()
    {
        return Instant.now().isAfter(expiresAt);
    }

    public UUID getSessionId()
    {
        return this.sessionId;
    }

    public UUID getUserId()
    {
        return this.userId;
    }

    public SessionStatus getStatus()
    {
        return this.status;
    }

    public Instant getExpiresAt()
    {
        return this.expiresAt;
    }

    public Instant getCreatedAt()
    {
        return this.createdAt;
    }

}
