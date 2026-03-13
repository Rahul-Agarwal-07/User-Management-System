package com.cleanarch.adapters.out.security.jwt;

import com.cleanarch.domain.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.UUID;


public class JwtAdapter {

    private final SecretKey key;

    public JwtAdapter(String  key) {
        this.key = Keys.hmacShaKeyFor(key.getBytes());
    }


    public Claims parse(String token){
        try {

            return Jwts.parser()
                   .verifyWith(key)
                   .build()
                   .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception e)
        {
            throw new InvalidTokenException();
        }
    }

    public UUID extractSessionId(Claims claims)
    {
        String sid = claims.get("sid", String.class);

        if (sid == null) {
            throw new InvalidTokenException();
        }

        return UUID.fromString(sid);
    }

    public UUID extractUserId(Claims claims)
    {
        String userId =  claims.getSubject();

        if(userId == null) throw new InvalidTokenException();

        return UUID.fromString(userId);
    }
}
