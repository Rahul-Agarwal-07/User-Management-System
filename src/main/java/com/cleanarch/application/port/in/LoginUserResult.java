package com.cleanarch.application.port.in;

import java.util.UUID;

public record LoginUserResult(
        String accessToken,
        String refreshToken,
        UUID userId,
        String role
) { }
