package com.cleanarch.adapters.out.persistence.repository;

import com.cleanarch.adapters.out.persistence.entity.SessionEntity;
import com.cleanarch.adapters.out.persistence.mapper.SessionMapper;
import com.cleanarch.application.port.out.SessionRepositoryPort;
import com.cleanarch.domain.model.Session;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SessionRepositoryAdapter implements SessionRepositoryPort {

    private final JpaSessionRepository jpaSessionRepository;

    public SessionRepositoryAdapter(JpaSessionRepository jpaSessionRepository) {
        this.jpaSessionRepository = jpaSessionRepository;
    }

    @Override
    public void save(Session session) {

        SessionEntity entity = SessionMapper.toEntity(session);
        jpaSessionRepository.save(entity);
    }

    @Override
    public Optional<Session> findById(UUID sessionId) {

        return jpaSessionRepository.findById(sessionId)
                .map(SessionMapper::toDomain);

    }

    @Override
    public List<Session> findActiveSessionsByUserId(UUID userId) {
        return jpaSessionRepository.findByUserIdAndStatus(userId, "ACTIVE")
                .stream()
                .map(SessionMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void revokeAllByUserId(UUID userId) {
        List<SessionEntity> sessions = jpaSessionRepository.findByUserIdAndStatus(userId, "ACTIVE");

        List<SessionEntity> updatedSessions = sessions.stream()
                .map(SessionMapper::toDomain)
                .peek(Session::revoke)
                .map(SessionMapper::toEntity)
                .toList();

        jpaSessionRepository.saveAll(updatedSessions);

    }
}
