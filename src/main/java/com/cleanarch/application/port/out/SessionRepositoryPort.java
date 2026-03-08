package com.cleanarch.application.port.out;

import com.cleanarch.domain.model.Session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepositoryPort {

    void save(Session session);

    Optional<Session> findById(UUID sessionId);

    List<Session> findActiveSessionsByUserId(UUID userId);

    void revokeAllByUserId(UUID userId);

}
