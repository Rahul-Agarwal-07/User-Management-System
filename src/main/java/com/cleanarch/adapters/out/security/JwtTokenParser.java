package com.cleanarch.adapters.out.security;

import com.cleanarch.application.port.out.TokenParserPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.UUID;

public class JwtTokenParser implements TokenParserPort {

    private final SecretKey key;

    public JwtTokenParser(String key) {
        this.key = Keys.hmacShaKeyFor(key.getBytes());
    }

    @Override
    public UUID extractSessionId(String refreshToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(refreshToken)
                .getBody();

        return UUID.fromString(claims.get("sid", String.class));
    }
}
