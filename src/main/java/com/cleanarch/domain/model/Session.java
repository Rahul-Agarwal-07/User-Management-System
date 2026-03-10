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

    private String userAgent;
    private String ipAddress;
    private String deviceId;

    private Instant createdAt;
    private Instant lastUsedAt;
    private Instant expiresAt;

    private SessionStatus status;

    private Session(
            UUID sessionId,
            UUID userId,
            String currRefreshTokenHash,
            String prevRefreshTokenHash,
            SessionStatus status,
            Instant createdAt,
            Instant expiresAt,
            Instant lastUsedAt,
            String deviceId,
            String ipAddress,
            String userAgent
    ) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.currTokenHash = currRefreshTokenHash;
        this.prevTokenHash = prevRefreshTokenHash;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.lastUsedAt = lastUsedAt;
        this.deviceId = deviceId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public static Session create(
            UUID sessionId,
            UUID userId,
            String refreshTokenHash,
            Instant expiresAt,
            String deviceId,
            String ipAddress,
            String userAgent
    )
    {
        return new Session(
                sessionId,
                userId,
                refreshTokenHash,
                null,
                SessionStatus.ACTIVE,
                Instant.now(),
                expiresAt,
                Instant.now(),
                deviceId,
                ipAddress,
                userAgent
        );
    }

    public static Session restore(
            UUID sessionId,
            UUID userId,
            String currRefreshTokenHash,
            String prevRefreshTokenHash,
            String status,
            Instant createdAt,
            Instant expiresAt,
            Instant lastUsedAt,
            String deviceId,
            String ipAddress,
            String userAgent
    ){
        return new Session(
                sessionId,
                userId,
                currRefreshTokenHash,
                prevRefreshTokenHash,
                SessionStatus.valueOf(status),
                createdAt,
                expiresAt,
                lastUsedAt,
                deviceId,
                ipAddress,
                userAgent
        );
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

    public String getCurrTokenHash() { return this.currTokenHash; }

    public String getPrevTokenHash() { return this.prevTokenHash; }

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

    public Instant getLastUsedAt() { return this.lastUsedAt; }

    public String getUserAgent() { return this.userAgent; }

    public String getIpAddress() { return this.ipAddress; }

    public String getDeviceId() { return this.deviceId; }

}
