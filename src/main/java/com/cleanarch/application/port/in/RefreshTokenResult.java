package com.cleanarch.application.port.in;

public record RefreshTokenResult(
        String refreshToken,
        String accessToken
) { }
