package com.cleanarch.application.port.in;

public record LoginUserCommand(
        String email,
        String password
) { }
