package com.cleanarch.adapters.in.web.dto;

public record LoginUserResponse(
        String accessToken,
        String userId,
        String role
){ }
