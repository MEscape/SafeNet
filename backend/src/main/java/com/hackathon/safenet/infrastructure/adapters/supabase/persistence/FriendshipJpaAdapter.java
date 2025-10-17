package com.hackathon.safenet.infrastructure.adapters.supabase.persistence;

import com.hackathon.safenet.domain.model.Friendship;
import com.hackathon.safenet.domain.model.User;
import com.hackathon.safenet.domain.ports.outbound.FriendshipRepositoryPort;
import com.hackathon.safenet.infrastructure.adapters.supabase.entity.FriendshipEntity;
import com.hackathon.safenet.infrastructure.adapters.supabase.entity.UserEntity;
import com.hackathon.safenet.infrastructure.adapters.supabase.mapper.EntityMapper;
import com.hackathon.safenet.infrastructure.adapters.supabase.repository.FriendshipJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendshipJpaAdapter implements FriendshipRepositoryPort {

    private final FriendshipJpaRepository jpaRepository;
    private final EntityMapper<Friendship, FriendshipEntity> friendshipMapper;
    private final EntityMapper<User, UserEntity> userMapper;

    @Override
    public Friendship save(Friendship friendship) {
        log.debug("Saving friendship: user1Id={}, user2Id={}",
                friendship.user1Id(), friendship.user2Id());
        FriendshipEntity entity = friendshipMapper.toEntity(friendship);
        FriendshipEntity saved = jpaRepository.save(entity);
        return friendshipMapper.toDomain(saved);
    }

    @Override
    public Optional<Friendship> findById(UUID id) {
        log.debug("Finding friendship by id: {}", id);
        return jpaRepository.findById(id)
                .map(friendshipMapper::toDomain);
    }

    @Override
    public List<User> findFriendsByUserId(UUID userId) {
        log.debug("Finding friends by user id: {}", userId);
        return jpaRepository.findAllByUserId(userId).stream()
                .map(friendship -> friendship.getOtherUser(userId))
                .filter(Objects::nonNull)
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public List<Friendship> findFriendshipsByUserId(UUID userId) {
        log.debug("Finding friendships by user id: {}", userId);
        return jpaRepository.findAllByUserId(userId).stream()
                .map(friendshipMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsBetweenUsers(UUID user1Id, UUID user2Id) {
        log.debug("Checking if users are friends: {} and {}", user1Id, user2Id);
        return jpaRepository.existsBetweenUsers(user1Id, user2Id);
    }

    @Override
    public void deleteBetweenUsers(UUID user1Id, UUID user2Id) {
        log.debug("Deleting friendship between users: {} and {}", user1Id, user2Id);
        jpaRepository.deleteBetweenUsers(user1Id, user2Id);
    }

    @Override
    public long countFriendsByUserId(UUID userId) {
        log.debug("Counting friends for user: {}", userId);
        return jpaRepository.countFriendsByUserId(userId);
    }

    @Override
    public List<Friendship> findRecentFriendshipsByUserId(UUID userId, Instant since) {
        log.debug("Finding recent friendships for user {} since {}", userId, since);

        return jpaRepository.findRecentFriendshipsByUserId(userId, since).stream()
                .map(friendshipMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findMutualFriends(UUID user1Id, UUID user2Id) {
        log.debug("Finding mutual friends between users: {} and {}", user1Id, user2Id);

        // Get friends of both users
        List<User> user1Friends = findFriendsByUserId(user1Id);
        List<User> user2Friends = findFriendsByUserId(user2Id);

        // Find intersection (mutual friends)
        return user1Friends.stream()
                .filter(user2Friends::contains)
                .toList();
    }
}