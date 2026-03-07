package com.cleanarch.application.port.out;

import java.util.UUID;

public interface TokenGeneratorPort {

    String generateAccessToken(UUID userId, String role);
    String generateRefreshToken(UUID userId);

}
