package com.hackathon.safenet.domain.ports.outbound;

import com.hackathon.safenet.domain.model.Friendship;
import com.hackathon.safenet.domain.model.User;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for friendship persistence operations.
 *
 * <p>This interface defines the operations required to persist and query
 * friendships between users. It abstracts the infrastructure layer
 * (JPA, database, etc.) from the domain and application layers.</p>
 */
public interface FriendshipRepositoryPort {

    /**
     * Save or update a friendship.
     *
     * @param friendship the friendship to save
     * @return the saved friendship
     */
    Friendship save(Friendship friendship);

    /**
     * Find a friendship by its ID.
     *
     * @param id the friendship ID
     * @return an optional containing the friendship if found
     */
    Optional<Friendship> findById(UUID id);

    /**
     * Find all friends of a user (returns User objects).
     *
     * @param userId the user ID
     * @return list of friend User objects
     */
    List<User> findFriendsByUserId(UUID userId);

    /**
     * Find all friendships of a user.
     *
     * @param userId the user ID
     * @return list of friendships
     */
    List<Friendship> findFriendshipsByUserId(UUID userId);

    /**
     * Check if two users are friends.
     *
     * @param user1Id first user ID
     * @param user2Id second user ID
     * @return true if they are friends
     */
    boolean existsBetweenUsers(UUID user1Id, UUID user2Id);

    /**
     * Delete a friendship between two users.
     *
     * @param user1Id the first user ID
     * @param user2Id the second user ID
     */
    void deleteBetweenUsers(UUID user1Id, UUID user2Id);

    /**
     * Count the number of friends for a user.
     *
     * @param userId the user ID
     * @return number of friends
     */
    long countFriendsByUserId(UUID userId);

    /**
     * Find recent friendships for a user created after a specific date.
     *
     * @param userId the user ID
     * @param since the date threshold (friendships created after this date)
     * @return list of recent friendships
     */
    List<Friendship> findRecentFriendshipsByUserId(UUID userId, Instant since);

    /**
     * Find mutual friends between two users.
     *
     * @param user1Id the first user ID
     * @param user2Id the second user ID
     * @return list of mutual friend User objects
     */
    List<User> findMutualFriends(UUID user1Id, UUID user2Id);
}