package com.cleanarch.application.port.in;

public record LoginUserCommand(
        String email,
        String password,
        String deviceId,
        String userAgent,
        String ipAddress
) { }
