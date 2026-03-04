package com.cleanarch.application.port.out;

public interface TokenGeneratorPort {

    String generateToken(String secretKey, String uniqueId);

}
