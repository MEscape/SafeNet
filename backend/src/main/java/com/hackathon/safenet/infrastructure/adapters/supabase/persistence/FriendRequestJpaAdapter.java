package com.hackathon.safenet.infrastructure.adapters.supabase.persistence;

import com.hackathon.safenet.domain.model.FriendRequest;
import com.hackathon.safenet.domain.ports.outbound.FriendRequestRepositoryPort;
import com.hackathon.safenet.infrastructure.adapters.supabase.entity.FriendRequestEntity;
import com.hackathon.safenet.infrastructure.adapters.supabase.entity.UserEntity;
import com.hackathon.safenet.infrastructure.adapters.supabase.mapper.EntityMapper;
import com.hackathon.safenet.infrastructure.adapters.supabase.repository.FriendRequestJpaRepository;
import com.hackathon.safenet.infrastructure.adapters.supabase.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendRequestJpaAdapter implements FriendRequestRepositoryPort {

    private final FriendRequestJpaRepository jpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final EntityMapper<FriendRequest, FriendRequestEntity> friendRequestMapper;

    @Override
    public FriendRequest save(FriendRequest friendRequest) {
        log.debug("Saving friend request: requesterId={}, requestedId={}",
                friendRequest.requesterId(), friendRequest.requestedId());
        FriendRequestEntity entity = friendRequestMapper.toEntity(friendRequest);
        FriendRequestEntity saved = jpaRepository.save(entity);
        return friendRequestMapper.toDomain(saved);
    }

    @Override
    public Optional<FriendRequest> findById(UUID id) {
        log.debug("Finding friend request by id: {}", id);
        return jpaRepository.findById(id)
                .map(friendRequestMapper::toDomain);
    }

    @Override
    public List<FriendRequest> findByRequesterId(UUID requesterId) {
        log.debug("Finding friend requests by requester: {}", requesterId);

        UserEntity requester = userJpaRepository.findById(requesterId).orElse(null);
        if (requester == null) {
            return List.of();
        }

        return jpaRepository.findByRequester(requester).stream()
                .map(friendRequestMapper::toDomain)
                .toList();
    }

    @Override
    public List<FriendRequest> findByRequestedId(UUID requestedId) {
        log.debug("Finding friend requests by requested: {}", requestedId);

        UserEntity requested = userJpaRepository.findById(requestedId).orElse(null);
        if (requested == null) {
            return List.of();
        }

        return jpaRepository.findByRequested(requested).stream()
                .map(friendRequestMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsBetweenUsers(UUID user1Id, UUID user2Id) {
        log.debug("Checking if friend request exists between users: {} and {}", user1Id, user2Id);
        return jpaRepository.existsBetweenUsers(user1Id, user2Id);
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting friend request by id: {}", id);
        jpaRepository.deleteById(id);
    }

    @Override
    public long countPendingReceivedRequests(UUID userId) {
        log.debug("Getting pending received requests count for user {}", userId);
        return jpaRepository.countPendingReceivedRequests(userId);
    }

    @Override
    public long countPendingSentRequests(UUID userId) {
        log.debug("Getting pending sent requests count for user {}", userId);
        return jpaRepository.countPendingSentRequests(userId);
    }

    @Override
    public Optional<FriendRequest> findBetweenUsers(UUID user1Id, UUID user2Id) {
        log.debug("Finding friend request between users: {} and {}", user1Id, user2Id);
        FriendRequestEntity entity = jpaRepository.findBetweenUsers(user1Id, user2Id);
        return entity != null ? Optional.of(friendRequestMapper.toDomain(entity)) : Optional.empty();
    }
}