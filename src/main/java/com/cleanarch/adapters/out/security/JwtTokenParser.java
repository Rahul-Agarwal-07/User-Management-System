package com.cleanarch.adapters.out.security;

import com.cleanarch.application.port.out.TokenParserPort;
import com.cleanarch.domain.exception.InvalidRefreshTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.UUID;

public class JwtTokenParser implements TokenParserPort {

    private final SecretKey key;

    public JwtTokenParser(String key) {
        this.key = Keys.hmacShaKeyFor(key.getBytes());
    }

    @Override
    public UUID extractSessionId(String refreshToken) {

        try {

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();

            return UUID.fromString(claims.get("sid", String.class));

        } catch (Exception e)
        {
            throw new InvalidRefreshTokenException();
        }
    }
}
