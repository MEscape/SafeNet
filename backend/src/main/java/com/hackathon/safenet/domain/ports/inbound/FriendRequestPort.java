package com.hackathon.safenet.domain.ports.inbound;

import com.hackathon.safenet.domain.model.FriendRequest;

import java.util.List;
import java.util.UUID;

public interface FriendRequestPort {

    /**
     * Send a friend request to another user
     *
     * @param requesterId ID of the user sending the request
     * @param requestedId ID of the user receiving the request
     * @return Created friend request
     * @throws IllegalArgumentException if users are the same, already friends, or request already exists
     */
    FriendRequest sendFriendRequest(UUID requesterId, UUID requestedId);

    /**
     * Accept a friend request
     *
     * @param requestId ID of the friend request to accept
     * @param userId ID of the user accepting the request (must be the requested user)
     * @return Updated friend request
     * @throws IllegalArgumentException if request not found or user not authorized
     */
    FriendRequest acceptFriendRequest(UUID requestId, UUID userId);

    /**
     * Reject a friend request
     *
     * @param requestId ID of the friend request to reject
     * @param userId ID of the user rejecting the request (must be the requested user)
     * @return Updated friend request
     * @throws IllegalArgumentException if request not found or user not authorized
     */
    FriendRequest rejectFriendRequest(UUID requestId, UUID userId);

    /**
     * Cancel a sent friend request
     *
     * @param requestId ID of the friend request to cancel
     * @param userId ID of the user canceling the request (must be the requester)
     * @throws IllegalArgumentException if request not found or user not authorized
     */
    void cancelFriendRequest(UUID requestId, UUID userId);

    /**
     * Get all pending friend requests received by a user
     *
     * @param userId ID of the user
     * @return List of pending friend requests
     */
    List<FriendRequest> getPendingReceivedRequests(UUID userId);

    /**
     * Get all pending friend requests sent by a user
     *
     * @param userId ID of the user
     * @return List of pending friend requests
     */
    List<FriendRequest> getPendingSentRequests(UUID userId);

    /**
     * Get the number of pending friend requests **received by** a specific user.
     *
     * @param userId the ID of the user receiving friend requests
     * @return the count of pending friend requests that the user has received
     */
    long getPendingReceivedRequestsCount(UUID userId);

    /**
     * Get the number of pending friend requests **sent by** a specific user.
     *
     * @param userId the ID of the user who sent friend requests
     * @return the count of pending friend requests that the user has sent
     */
    long getPendingSentRequestsCount(UUID userId);
}