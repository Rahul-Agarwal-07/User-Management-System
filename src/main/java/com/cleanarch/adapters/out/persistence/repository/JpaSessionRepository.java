package com.cleanarch.adapters.out.persistence.repository;

import com.cleanarch.adapters.out.persistence.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaSessionRepository extends JpaRepository<SessionEntity, UUID> {

    List<SessionEntity> findByUserIdAndStatus(UUID userId, String status);
    Optional<SessionEntity> findBySessionIdAndUserId(UUID sessionId, UUID userId);

}
