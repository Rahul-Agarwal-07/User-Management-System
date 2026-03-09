package com.cleanarch.adapters.out.security;

import com.cleanarch.application.port.out.PasswordHasherPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptPasswordHasherAdapter implements PasswordHasherPort {


    private final BCryptPasswordEncoder passwordEncoder;

    public BcryptPasswordHasherAdapter(BcryptPasswordHasherAdapter passwordEncoder) {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String hash(String rawPass) {
        return passwordEncoder.encode(rawPass);
    }

    @Override
    public boolean matches(String rawPass, String passHash) {

        return passwordEncoder.matches(rawPass, passHash);
    }
}
