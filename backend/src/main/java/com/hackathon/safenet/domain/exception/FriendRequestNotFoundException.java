package com.hackathon.safenet.domain.exception;

/**
 * Exception thrown when a friend request is not found.
 * 
 * <p>This exception is thrown when attempting to perform operations on
 * a friend request that does not exist in the system.</p>
 * 
 * @author SafeNet Development Team
 * @since 1.0.0
 */
public class FriendRequestNotFoundException extends FriendshipException {
    
    /**
     * Constructs a new friend request not found exception.
     * 
     * @param requestId the ID of the friend request that was not found
     */
    public FriendRequestNotFoundException(String requestId) {
        super("error.friend_request.not_found", requestId);
    }
    
    /**
     * Constructs a new friend request not found exception for user-specific requests.
     * 
     * @param requesterId the ID of the requester
     * @param requestedId the ID of the requested user
     */
    public FriendRequestNotFoundException(String requesterId, String requestedId) {
        super("error.friend_request.not_found_between_users", requesterId, requestedId);
    }
}