package com.cleanarch.adapters.out.persistence.mapper;

import com.cleanarch.adapters.out.persistence.entity.UserEntity;
import com.cleanarch.domain.model.User;
import com.cleanarch.domain.model.UserStatus;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getPhone(),
                user.getStatus().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public User toDomain(UserEntity entity) {
        return User.restore(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getPasswordHash(),
                UserStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}