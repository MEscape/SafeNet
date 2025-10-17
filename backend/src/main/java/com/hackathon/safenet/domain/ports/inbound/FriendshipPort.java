package com.hackathon.safenet.domain.ports.inbound;

import com.hackathon.safenet.domain.model.Friendship;
import com.hackathon.safenet.domain.model.User;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Inbound port for friendship management operations.
 *
 * <p>This port defines the operations available to the application layer
 * for managing friendships between users. It abstracts the underlying
 * persistence and infrastructure details.</p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Retrieve friends and friendships</li>
 *   <li>Check friendship status</li>
 *   <li>Manage friendship relationships</li>
 *   <li>Retrieve friendship-related statistics</li>
 * </ul>
 *
 * @author SafeNet
 * @since 1.0.0
 */
public interface FriendshipPort {

    /**
     * Get all friends of a user.
     *
     * @param userId the ID of the user
     * @return list of friends
     */
    List<User> getFriends(UUID userId);

    /**
     * Get all friendships involving a user.
     *
     * @param userId the ID of the user
     * @return list of friendships
     */
    List<Friendship> getFriendships(UUID userId);

    /**
     * Check if two users are friends.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     * @return true if users are friends, false otherwise
     */
    boolean areFriends(UUID userId1, UUID userId2);

    /**
     * Remove a friendship between two users.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     * @param removedByUserId the ID of the user who initiated the removal
     */
    void removeFriendship(UUID userId1, UUID userId2, UUID removedByUserId);

    /**
     * Get mutual friends between two users.
     *
     * @param userId1 the ID of the first user
     * @param userId2 the ID of the second user
     * @return list of mutual friends
     */
    List<User> getMutualFriends(UUID userId1, UUID userId2);

    /**
     * Get the number of friends for a user.
     *
     * @param userId the ID of the user
     * @return count of friends
     */
    long getFriendCount(UUID userId);

    /**
     * Get recent friendships for a user created after a specific timestamp.
     *
     * @param userId the ID of the user
     * @param since timestamp to filter friendships created after
     * @return list of recent friendships
     */
    List<Friendship> getRecentFriendships(UUID userId, Instant since);
}
