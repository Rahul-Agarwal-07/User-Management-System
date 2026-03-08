package com.cleanarch.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest(
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotBlank String deviceId,
        @NotBlank String ipAddress
) { }
