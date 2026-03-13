package com.cleanarch.adapters.out.security.principal;

import java.util.UUID;

public record AuthUser(
        UUID sessionId,
        UUID userId
) {
}
