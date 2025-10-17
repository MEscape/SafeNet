package com.hackathon.safenet.application.service;


import com.hackathon.safenet.domain.model.FriendRequest;
import com.hackathon.safenet.domain.model.Friendship;
import com.hackathon.safenet.domain.model.User;
import com.hackathon.safenet.domain.ports.inbound.FriendshipPort;
import com.hackathon.safenet.domain.ports.outbound.FriendRequestRepositoryPort;
import com.hackathon.safenet.domain.ports.outbound.FriendshipRepositoryPort;
import com.hackathon.safenet.domain.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for friendship management operations.
 * 
 * <p>This service handles all friendship-related operations including retrieving
 * friends, managing friendships, and providing friendship statistics.</p>
 * 
 * <h3>Key Features</h3>
 * <ul>
 *   <li><strong>Friend Management:</strong> Comprehensive friendship operations and queries</li>
 *   <li><strong>Validation:</strong> Proper validation for user existence and friendship status</li>
 *   <li><strong>Statistics:</strong> Friend counts, mutual friends, and recent friendship data</li>
 *   <li><strong>Performance:</strong> Optimized queries for large friend networks</li>
 * </ul>
 * 
 * <h3>Friendship Model</h3>
 * <p>Friendships are bidirectional relationships stored as a single record with
 * user1Id and user2Id. The service handles the bidirectional nature transparently,
 * allowing queries from either user's perspective.</p>
 * 
 * @author SafeNet Development Team
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipService implements FriendshipPort {

    private final FriendshipRepositoryPort friendshipRepository;
    private final UserRepositoryPort userRepository;
    private final FriendRequestRepositoryPort friendRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> getFriends(UUID userId) {
        log.debug("Getting friends for user: {}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        return friendshipRepository.findFriendsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friendship> getFriendships(UUID userId) {
        log.debug("Getting friendships for user: {}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        return friendshipRepository.findFriendshipsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean areFriends(UUID userId1, UUID userId2) {
        log.debug("Checking friendship between users: {} and {}", userId1, userId2);

        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }
        if (userId1.equals(userId2)) {
            return false;
        }

        return friendshipRepository.existsBetweenUsers(userId1, userId2);
    }

    @Override
    public void removeFriendship(UUID userId1, UUID userId2, UUID removedByUserId) {
        log.info("Removing friendship between users: {} and {}, removed by: {}", userId1, userId2, removedByUserId);

        if (userId1 == null || userId2 == null || removedByUserId == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Cannot remove friendship with yourself");
        }

        if (!userId1.equals(removedByUserId) && !userId2.equals(removedByUserId)) {
            throw new IllegalArgumentException("Only friends can remove their friendship");
        }

        // Verify both users exist
        userRepository.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId1));
        userRepository.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId2));

        // Verify friendship exists
        if (!friendshipRepository.existsBetweenUsers(userId1, userId2)) {
            throw new IllegalArgumentException("Friendship does not exist between these users");
        }

        // Remove the friendship
        friendshipRepository.deleteBetweenUsers(userId1, userId2);

        // Reset the friend request status to PENDING so users can send requests again
        friendRequestRepository.findBetweenUsers(userId1, userId2)
                .ifPresent(friendRequest -> {
                    FriendRequest resetRequest = friendRequest.resetToPending();
                    friendRequestRepository.save(resetRequest);
                    log.info("Reset friend request status to PENDING between users: {} and {}", userId1, userId2);
                });

        log.info("Successfully removed friendship between users: {} and {}", userId1, userId2);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getMutualFriends(UUID userId1, UUID userId2) {
        log.debug("Getting mutual friends between users: {} and {}", userId1, userId2);

        if (userId1 == null || userId2 == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Cannot get mutual friends with yourself");
        }

        // Verify both users exist
        if (!userRepository.existsById(userId1)) {
            throw new IllegalArgumentException("User not found: " + userId1);
        }
        if (!userRepository.existsById(userId2)) {
            throw new IllegalArgumentException("User not found: " + userId2);
        }

        return friendshipRepository.findMutualFriends(userId1, userId2);
    }

    @Override
    @Transactional(readOnly = true)
    public long getFriendCount(UUID userId) {
        log.debug("Getting friend count for user: {}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        return friendshipRepository.countFriendsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Friendship> getRecentFriendships(UUID userId, Instant since) {
        log.debug("Getting recent friendships for user: {} since {}", userId, since);

        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        return friendshipRepository.findRecentFriendshipsByUserId(userId, since);
    }
}