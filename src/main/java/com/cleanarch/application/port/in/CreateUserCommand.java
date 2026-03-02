package com.cleanarch.application.port.in;

public record CreateUserCommand(
        String name,
        String email,
        String phone,
        String password
) {
}
