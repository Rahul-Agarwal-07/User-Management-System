package com.cleanarch.application.port.out;

import com.cleanarch.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findById(UUID id);
    UUID save(User user);

}
