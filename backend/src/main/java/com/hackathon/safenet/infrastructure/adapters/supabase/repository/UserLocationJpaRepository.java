package com.hackathon.safenet.infrastructure.adapters.supabase.repository;

import com.hackathon.safenet.infrastructure.adapters.supabase.entity.UserLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link UserLocationEntity}.
 * <p>
 * This interface extends {@link JpaRepository} providing basic CRUD operations,
 * as well as custom query methods for finding user locations with various criteria.
 * <p>
 * Spring Data JPA automatically generates the implementation at runtime.
 */
@Repository
public interface UserLocationJpaRepository extends JpaRepository<UserLocationEntity, UUID> {

    /**
     * Find latest location by user ID
     *
     * @param userId the user ID
     * @return optional user location
     */
    @Query("SELECT ul FROM UserLocationEntity ul WHERE ul.user.id = :userId ORDER BY ul.updatedAt DESC LIMIT 1")
    Optional<UserLocationEntity> findByUserId(@Param("userId") UUID userId);

    /**
     * Find latest visible location by user ID
     *
     * @param userId the user ID
     * @return optional visible user location
     */
    @Query("SELECT ul FROM UserLocationEntity ul WHERE ul.user.id = :userId AND ul.visibleToFriends = true ORDER BY ul.updatedAt DESC LIMIT 1")
    Optional<UserLocationEntity> findVisibleByUserId(@Param("userId") UUID userId);

    /**
     * Find latest visible locations of specific users
     *
     * @param userIds list of user IDs
     * @return list of visible user locations for the specified users
     */
    @Query("SELECT ul FROM UserLocationEntity ul WHERE ul.user.id IN :userIds AND ul.visibleToFriends = true")
    List<UserLocationEntity> findVisibleLocationsByUserIds(@Param("userIds") List<UUID> userIds);

    /**
     * Find locations within a bounding box
     *
     * @param minLat minimum latitude
     * @param maxLat maximum latitude
     * @param minLon minimum longitude
     * @param maxLon maximum longitude
     * @return list of locations within the bounding box
     */
    @Query("SELECT ul FROM UserLocationEntity ul WHERE " +
            "ul.latitude BETWEEN :minLat AND :maxLat AND " +
            "ul.longitude BETWEEN :minLon AND :maxLon AND " +
            "ul.visibleToFriends = true")
    List<UserLocationEntity> findWithinBounds(
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLon") BigDecimal minLon,
            @Param("maxLon") BigDecimal maxLon);

    /**
     * Find recent locations (updated after specified time)
     *
     * @param since the timestamp to compare against
     * @return list of recently updated locations
     */
    @Query("SELECT ul FROM UserLocationEntity ul WHERE ul.updatedAt > :since ORDER BY ul.updatedAt DESC")
    List<UserLocationEntity> findRecentLocations(@Param("since") Instant since);

    /**
     * Find stale locations (updated before specified time)
     *
     * @param before the timestamp to compare against
     * @return list of stale locations
     */
    @Query("SELECT ul FROM UserLocationEntity ul WHERE ul.updatedAt < :before ORDER BY ul.updatedAt ASC")
    List<UserLocationEntity> findStaleLocations(@Param("before") Instant before);

    /**
     * Check if user has a recent location record
     *
     * @param userId the user ID
     * @param since the timestamp to compare against
     * @return true if user has a recent location record
     */
    @Query("SELECT COUNT(ul) > 0 FROM UserLocationEntity ul WHERE ul.user.id = :userId AND ul.updatedAt > :since")
    boolean hasRecentLocation(@Param("userId") UUID userId, @Param("since") Instant since);

    /**
     * Delete all locations by user ID
     *
     * @param userId the user ID
     */
    @Modifying
    @Query("DELETE FROM UserLocationEntity ul WHERE ul.user.id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    /**
     * Delete old locations (updated before specified time)
     *
     * @param before the timestamp to compare against
     */
    @Modifying
    @Query("DELETE FROM UserLocationEntity ul WHERE ul.updatedAt < :before")
    void deleteOldLocations(@Param("before") Instant before);
}