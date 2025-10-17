package com.hackathon.safenet.infrastructure.adapters.supabase.mapper;

import com.hackathon.safenet.domain.model.FriendRequest;
import com.hackathon.safenet.infrastructure.adapters.supabase.entity.FriendRequestEntity;
import com.hackathon.safenet.infrastructure.adapters.supabase.entity.UserEntity;
import com.hackathon.safenet.infrastructure.adapters.supabase.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Mapper component for converting between FriendRequest domain model and FriendRequestEntity
 */
@Component
@RequiredArgsConstructor
public class FriendRequestMapperImpl implements EntityMapper<FriendRequest, FriendRequestEntity> {

    private final UserJpaRepository userJpaRepository;

    @Override
    public FriendRequest toDomain(FriendRequestEntity entity) {
        if (entity == null) {
            return null;
        }

        return new FriendRequest(
                entity.getId(),
                entity.getRequester().getId(),
                entity.getRequested().getId(),
                mapStatusToDomain(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    @Override
    public FriendRequestEntity toEntity(FriendRequest domain) {
        if (domain == null) {
            return null;
        }

        // Fetch user entities
        UserEntity requester = userJpaRepository.findById(domain.requesterId())
                .orElseThrow(() -> new IllegalArgumentException("Requester user not found: " + domain.requesterId()));
        UserEntity requested = userJpaRepository.findById(domain.requestedId())
                .orElseThrow(() -> new IllegalArgumentException("Requested user not found: " + domain.requestedId()));

        return FriendRequestEntity.builder()
                .id(domain.id())
                .requester(requester)
                .requested(requested)
                .status(mapStatusToEntity(domain.status()))
                .createdAt(domain.createdAt())
                .updatedAt(domain.updatedAt() != null ? domain.updatedAt() : Instant.now())
                .build();
    }

    /**
     * Map entity status to domain status
     */
    private FriendRequest.FriendRequestStatus mapStatusToDomain(FriendRequestEntity.FriendRequestStatus entityStatus) {
        if (entityStatus == null) {
            return null;
        }

        return switch (entityStatus) {
            case PENDING -> FriendRequest.FriendRequestStatus.PENDING;
            case ACCEPTED -> FriendRequest.FriendRequestStatus.ACCEPTED;
            case REJECTED -> FriendRequest.FriendRequestStatus.REJECTED;
        };
    }

    /**
     * Map domain status to entity status
     */
    private FriendRequestEntity.FriendRequestStatus mapStatusToEntity(FriendRequest.FriendRequestStatus domainStatus) {
        if (domainStatus == null) {
            return null;
        }

        return switch (domainStatus) {
            case PENDING -> FriendRequestEntity.FriendRequestStatus.PENDING;
            case ACCEPTED -> FriendRequestEntity.FriendRequestStatus.ACCEPTED;
            case REJECTED -> FriendRequestEntity.FriendRequestStatus.REJECTED;
        };
    }
}