package com.hackathon.safenet.domain.exception;

/**
 * Exception thrown when a friendship is not found.
 * 
 * <p>This exception is thrown when attempting to perform operations on
 * a friendship that does not exist between two users.</p>
 * 
 * @author SafeNet Development Team
 * @since 1.0.0
 */
public class FriendshipNotFoundException extends FriendshipException {
    
    /**
     * Constructs a new friendship not found exception.
     * 
     * @param user1Id the ID of the first user
     * @param user2Id the ID of the second user
     */
    public FriendshipNotFoundException(String user1Id, String user2Id) {
        super("error.friendship.not_found", user1Id, user2Id);
    }
    
    /**
     * Constructs a new friendship not found exception with friendship ID.
     * 
     * @param friendshipId the ID of the friendship that was not found
     */
    public FriendshipNotFoundException(String friendshipId) {
        super("error.friendship.not_found_by_id", friendshipId);
    }
}