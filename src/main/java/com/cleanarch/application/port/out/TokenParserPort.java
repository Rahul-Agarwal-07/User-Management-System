package com.cleanarch.application.port.out;

import java.util.UUID;

public interface TokenParserPort {

    UUID extractSessionId(String refreshToken);

}
