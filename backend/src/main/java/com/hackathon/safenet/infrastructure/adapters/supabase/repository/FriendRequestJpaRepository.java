package com.hackathon.safenet.infrastructure.adapters.supabase.repository;

import com.hackathon.safenet.infrastructure.adapters.supabase.entity.FriendRequestEntity;
import com.hackathon.safenet.infrastructure.adapters.supabase.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link FriendRequestEntity}.
 * <p>
 * This interface extends {@link JpaRepository} providing basic CRUD operations,
 * as well as custom query methods for finding friend requests by various criteria.
 * <p>
 * Spring Data JPA automatically generates the implementation at runtime.
 */
@Repository
public interface FriendRequestJpaRepository extends JpaRepository<FriendRequestEntity, UUID> {

    /**
     * Find all friend requests sent by a specific user
     *
     * @param requesterUser the user who sent the requests
     * @return list of friend requests sent by the user
     */
    List<FriendRequestEntity> findByRequester(UserEntity requesterUser);

    /**
     * Find all friend requests received by a specific user
     *
     * @param requestedUser the user who received the requests
     * @return list of friend requests received by the user
     */
    List<FriendRequestEntity> findByRequested(UserEntity requestedUser);

    /**
     * Check if a friend request exists between two users (in either direction)
     *
     * @param user1Id first user ID
     * @param user2Id second user ID
     * @return true if a request exists in either direction
     */
    @Query("SELECT COUNT(fr) > 0 FROM FriendRequestEntity fr WHERE " +
            "(fr.requester.id = :user1Id AND fr.requested.id = :user2Id) OR " +
            "(fr.requester.id = :user2Id AND fr.requested.id = :user1Id)")
    boolean existsBetweenUsers(@Param("user1Id") UUID user1Id, @Param("user2Id") UUID user2Id);

    /**
     * Count pending friend requests **received by** a user
     */
    @Query("SELECT COUNT(fr) FROM FriendRequestEntity fr WHERE fr.requested.id = :userId AND fr.status = 'PENDING'")
    long countPendingReceivedRequests(@Param("userId") UUID userId);

    /**
     * Count pending friend requests **sent by** a user
     */
    @Query("SELECT COUNT(fr) FROM FriendRequestEntity fr WHERE fr.requester.id = :userId AND fr.status = 'PENDING'")
    long countPendingSentRequests(@Param("userId") UUID userId);

    /**
     * Find friend request between two users (in either direction)
     *
     * @param user1Id first user ID
     * @param user2Id second user ID
     * @return friend request entity if found
     */
    @Query("SELECT fr FROM FriendRequestEntity fr WHERE " +
            "(fr.requester.id = :user1Id AND fr.requested.id = :user2Id) OR " +
            "(fr.requester.id = :user2Id AND fr.requested.id = :user1Id)")
    FriendRequestEntity findBetweenUsers(@Param("user1Id") UUID user1Id, @Param("user2Id") UUID user2Id);
}