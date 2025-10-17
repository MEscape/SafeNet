package com.hackathon.safenet.infrastructure.adapters.supabase.mapper;

import com.hackathon.safenet.domain.model.UserLocation;
import com.hackathon.safenet.infrastructure.adapters.supabase.entity.UserLocationEntity;
import com.hackathon.safenet.infrastructure.adapters.supabase.entity.UserEntity;
import com.hackathon.safenet.infrastructure.adapters.supabase.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Mapper component for converting between UserLocation domain model and UserLocationEntity
 */
@Component
@RequiredArgsConstructor
public class UserLocationMapperImpl implements EntityMapper<UserLocation, UserLocationEntity> {

    private final UserJpaRepository userJpaRepository;

    @Override
    public UserLocation toDomain(UserLocationEntity entity) {
        if (entity == null) {
            return null;
        }

        return new UserLocation(
                entity.getId(),
                entity.getUser().getId(),
                entity.getLatitude(),
                entity.getLongitude(),
                entity.getAltitude(),
                entity.getAccuracy(),
                entity.getVisibleToFriends(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public UserLocationEntity toEntity(UserLocation domain) {
        if (domain == null) {
            return null;
        }

        // Fetch user entity
        UserEntity user = userJpaRepository.findById(domain.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + domain.userId()));

        return UserLocationEntity.builder()
                .id(domain.id())
                .user(user)
                .latitude(domain.latitude())
                .longitude(domain.longitude())
                .altitude(domain.altitude())
                .accuracy(domain.accuracy())
                .visibleToFriends(domain.visibleToFriends())
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt() != null ? domain.updatedAt() : Instant.now())
                .build();
    }
}