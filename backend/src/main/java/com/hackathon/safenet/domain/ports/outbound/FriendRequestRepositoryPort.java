package com.hackathon.safenet.domain.ports.outbound;

import com.hackathon.safenet.domain.model.FriendRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendRequestRepositoryPort {

    /**
     * Save or update friend request
     */
    FriendRequest save(FriendRequest friendRequest);

    /**
     * Find friend request by ID
     */
    Optional<FriendRequest> findById(UUID id);

    /**
     * Find all friend requests sent by a user
     */
    List<FriendRequest> findByRequesterId(UUID requesterId);

    /**
     * Find all friend requests received by a user
     */
    List<FriendRequest> findByRequestedId(UUID requestedId);

    /**
     * Check if friend request exists between two users (in either direction)
     */
    boolean existsBetweenUsers(UUID user1Id, UUID user2Id);

    /**
     * Delete friend request by ID
     */
    void deleteById(UUID id);

    /**
     * Count pending friend requests **received by** a user
     */
    long countPendingReceivedRequests(UUID userId);

    /**
     * Count pending friend requests **sent by** a user
     */
    long countPendingSentRequests(UUID userId);

    /**
     * Find friend request between two users (in either direction)
     */
    Optional<FriendRequest> findBetweenUsers(UUID user1Id, UUID user2Id);
}