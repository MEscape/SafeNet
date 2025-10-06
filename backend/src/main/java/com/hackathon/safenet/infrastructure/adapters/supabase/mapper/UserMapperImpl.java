package com.hackathon.safenet.infrastructure.adapters.supabase.mapper;

import com.hackathon.safenet.domain.model.User;
import com.hackathon.safenet.infrastructure.adapters.supabase.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapper component for converting between User domain model and UserEntity
 */
@Component
public class UserMapperImpl implements EntityMapper<User, UserEntity> {

    @Override
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new User(
                entity.getId(),
                entity.getAuthId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getMeta() != null ? Collections.unmodifiableMap(entity.getMeta()) : Map.of(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public UserEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return UserEntity.builder()
                .id(domain.id())
                .authId(domain.authId())
                .username(domain.username())
                .email(domain.email())
                .firstName(domain.firstName())
                .lastName(domain.lastName())
                .meta(domain.meta() != null ? new HashMap<>(domain.meta()) : Map.of())
                .createdAt(domain.createdAt())
                .updatedAt(Instant.now())
                .build();
    }
}