package com.cleanarch.adapters.in.web.dto;

public record RefreshTokenResponse(
        String refreshToken,
        String accessToken
) {
}
