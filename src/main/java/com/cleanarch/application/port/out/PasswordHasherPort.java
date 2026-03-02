package com.cleanarch.application.port.out;

public interface PasswordHasherPort {

    String hash(String rawPass);
    boolean matches(String rawPass, String passHash);

}
