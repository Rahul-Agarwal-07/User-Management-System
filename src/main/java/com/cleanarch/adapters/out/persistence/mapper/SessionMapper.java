package com.cleanarch.adapters.out.persistence.mapper;

import com.cleanarch.adapters.out.persistence.entity.SessionEntity;
import com.cleanarch.domain.model.Session;
import org.springframework.stereotype.Component;

@Component
public class SessionMapper {

    public static SessionEntity toEntity(Session session)
    {
        return new SessionEntity(
                session.getSessionId(),
                session.getUserId(),
                session.getCurrTokenHash(),
                session.getPrevTokenHash(),
                session.getStatus().name(),
                session.getCreatedAt(),
                session.getExpiresAt(),
                session.getLastUsedAt(),
                session.getDeviceId(),
                session.getIpAddress(),
                session.getUserAgent()
        );
    }

    public static Session toDomain(SessionEntity entity)
    {
        return Session.restore(
                entity.getSessionId(),
                entity.getUserId(),
                entity.getCurrRefreshTokenHash(),
                entity.getPrevRefreshTokenHash(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getExpiresAt(),
                entity.getLastUsedAt(),
                entity.getDeviceId(),
                entity.getIpAddress(),
                entity.getUserAgent()
        );
    }

}
