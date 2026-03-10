package com.cleanarch.adapters.out.security;

import com.cleanarch.application.port.out.TokenHasherPort;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.util.Base64;

@Component
public class Sha256TokenHasher implements TokenHasherPort {

    @Override
    public String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashed = digest.digest(rawToken.getBytes());

            return Base64.getEncoder().encodeToString(hashed);

        } catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
