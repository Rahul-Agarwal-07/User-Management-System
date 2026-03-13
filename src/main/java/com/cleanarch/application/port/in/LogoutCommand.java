package com.cleanarch.application.port.in;

import java.util.UUID;

public record LogoutCommand(
        UUID sessionId,
        UUID userId
) {
}
