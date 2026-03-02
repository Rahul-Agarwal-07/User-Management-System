package com.cleanarch.adapters.out.persistence.repository;

import com.cleanarch.adapters.out.persistence.entity.UserEntity;
import com.cleanarch.adapters.out.persistence.mapper.UserMapper;
import com.cleanarch.application.port.out.UserRepositoryPort;
import com.cleanarch.domain.exception.InvalidUserDataException;
import com.cleanarch.domain.exception.UserAlreadyExistsException;
import com.cleanarch.domain.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpaUserRepository;
    private final UserMapper mapper;

    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository, UserMapper mapper)
    {
        this.jpaUserRepository = jpaUserRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return jpaUserRepository.findByPhone(phone)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public UUID save(User user) {
        UserEntity userEntity = mapper.toEntity(user);

        try {
            return jpaUserRepository.save(userEntity).getId();
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("Email or Phone already exists");
        }
    }
}
