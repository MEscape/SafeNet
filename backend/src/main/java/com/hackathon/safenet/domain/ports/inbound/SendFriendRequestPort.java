package com.hackathon.safenet.domain.ports.inbound;

import com.hackathon.safenet.domain.exception.DuplicateFriendRequestException;
import com.hackathon.safenet.domain.exception.InvalidFriendRequestOperationException;
import com.hackathon.safenet.domain.model.FriendRequest;

/**
 * Port interface for sending friend requests.
 * 
 * <p>This port defines the contract for the send friend request use case.
 * It follows the hexagonal architecture pattern by providing a clear
 * boundary between the application core and external adapters.</p>
 * 
 * @author SafeNet Development Team
 * @since 1.0.0
 */
public interface SendFriendRequestPort {
    
    /**
     * Send a friend request from one user to another.
     * 
     * @param requesterId the ID of the user sending the request
     * @param requestedId the ID of the user receiving the request
     * @param message optional message to include with the request
     * @param language the preferred language for notifications
     * @return the created friend request
     * @throws InvalidFriendRequestOperationException if the request is invalid
     * @throws DuplicateFriendRequestException if a request already exists
     * @throws IllegalArgumentException if the users don't exist
     */
    FriendRequest sendFriendRequest(String requesterId, String requestedId, String message, String language);
}