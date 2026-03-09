package com.cleanarch.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "sessions")
public class SessionEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID sessionId;

    @Column(nullable = false, updatable = false)
    private UUID userId;

    @Column(nullable = false)
    private String currRefreshTokenHash;

    private String prevRefreshTokenHash;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant lastUsedAt;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String userAgent;

    protected SessionEntity() {}

    public SessionEntity(
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
        this.sessionId = sessionId;
        this.userId = userId;
        this.currRefreshTokenHash = currRefreshTokenHash;
        this.prevRefreshTokenHash = prevRefreshTokenHash;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.lastUsedAt = lastUsedAt;
        this.deviceId = deviceId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public UUID getSessionId() { return this.sessionId; }
    public UUID getUserId() { return this.userId; }
    public String getCurrRefreshTokenHash() { return this.currRefreshTokenHash; }
    public String getPrevRefreshTokenHash() { return this.prevRefreshTokenHash; }
    public String getStatus() { return this.status; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getExpiresAt() { return this.expiresAt; }
    public Instant getLastUsedAt() { return this.lastUsedAt; }
    public String getDeviceId() { return this.deviceId; }
    public String getIpAddress() { return this.ipAddress; }
    public String getUserAgent() { return this.userAgent; }


}
