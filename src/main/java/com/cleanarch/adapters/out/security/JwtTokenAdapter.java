package com.cleanarch.adapters.out.security;

import com.cleanarch.application.port.out.TokenGeneratorPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class JwtTokenAdapter implements TokenGeneratorPort {


    private final SecretKey key;

    private final long accessTokenExpiration = 15 * 60;
    private final long refreshTokenExpiration = 30 * 24 * 60 * 60;

    public JwtTokenAdapter(String key) {
        this.key = Keys.hmacShaKeyFor(key.getBytes());
    }

    @Override
    public String generateAccessToken(UUID userId, UUID sessionId, String role) {

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userId.toString())
                .claim("sid", sessionId)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(accessTokenExpiration)))
                .issuer("cleanarch-api")
                .signWith(key)
                .compact();

    }

    @Override
    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(refreshTokenExpiration)))
                .issuer("cleanarch-api")
                .signWith(key)
                .compact();
    }
}
