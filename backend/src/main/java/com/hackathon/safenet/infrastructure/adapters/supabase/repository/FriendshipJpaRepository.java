package com.hackathon.safenet.infrastructure.adapters.supabase.repository;

import com.hackathon.safenet.infrastructure.adapters.supabase.entity.FriendshipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link FriendshipEntity}.
 * <p>
 * This interface extends {@link JpaRepository} providing basic CRUD operations,
 * as well as custom query methods for finding friendships and friends.
 * <p>
 * Spring Data JPA automatically generates the implementation at runtime.
 */
@Repository
public interface FriendshipJpaRepository extends JpaRepository<FriendshipEntity, UUID> {

    /**
     * Find all friends of a specific user
     *
     * @param userId the user ID to find friends for
     * @return list of friendship entities where the user is involved
     */
    @Query("SELECT f FROM FriendshipEntity f WHERE f.user1.id = :userId OR f.user2.id = :userId")
    List<FriendshipEntity> findAllByUserId(@Param("userId") UUID userId);

    /**
     * Check if two users are friends
     *
     * @param user1Id first user ID
     * @param user2Id second user ID
     * @return true if the users are friends
     */
    @Query("SELECT COUNT(f) > 0 FROM FriendshipEntity f WHERE " +
            "(f.user1.id = :user1Id AND f.user2.id = :user2Id) OR " +
            "(f.user1.id = :user2Id AND f.user2.id = :user1Id)")
    boolean existsBetweenUsers(@Param("user1Id") UUID user1Id, @Param("user2Id") UUID user2Id);

    /**
     * Delete friendship between two users
     *
     * @param user1Id first user ID
     * @param user2Id second user ID
     */
    @Modifying
    @Query("DELETE FROM FriendshipEntity f WHERE " +
            "(f.user1.id = :user1Id AND f.user2.id = :user2Id) OR " +
            "(f.user1.id = :user2Id AND f.user2.id = :user1Id)")
    void deleteBetweenUsers(@Param("user1Id") UUID user1Id, @Param("user2Id") UUID user2Id);

    /**
     * Count the number of friends for a user
     *
     * @param userId the user ID
     * @return number of friends
     */
    @Query("SELECT COUNT(f) FROM FriendshipEntity f WHERE f.user1.id = :userId OR f.user2.id = :userId")
    long countFriendsByUserId(@Param("userId") UUID userId);

    /**
     * Find recent friendships created after a specific timestamp with limit
     *
     * @param userId the user ID
     * @param since the timestamp to compare against
     * @return list of recent friendships ordered by creation date descending
     */
    @Query(value = "SELECT f FROM FriendshipEntity f WHERE " +
            "(f.user1.id = :userId OR f.user2.id = :userId) AND " +
            "f.createdAt > :since " +
            "ORDER BY f.createdAt DESC")
    List<FriendshipEntity> findRecentFriendshipsByUserId(
            @Param("userId") UUID userId,
            @Param("since") Instant since);
}