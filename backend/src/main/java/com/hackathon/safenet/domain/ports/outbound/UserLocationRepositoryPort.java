package com.hackathon.safenet.domain.ports.outbound;

import com.hackathon.safenet.domain.model.UserLocation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserLocationRepositoryPort {

    /**
     * Save or update user location
     */
    UserLocation save(UserLocation userLocation);

    /**
     * Find user location by ID
     */
    Optional<UserLocation> findById(UUID id);

    /**
     * Find latest location for a user
     */
    Optional<UserLocation> findLatestByUserId(UUID userId);

    /**
     * Find latest visible location for a user
     */
    Optional<UserLocation> findLatestVisibleByUserId(UUID userId);

    /**
     * Find visible locations for multiple users (friends)
     */
    List<UserLocation> findLatestVisibleLocationsByUserIds(List<UUID> userIds);

    /**
     * Find locations within a bounding box
     */
    List<UserLocation> findWithinBounds(BigDecimal minLat, BigDecimal maxLat, 
                                       BigDecimal minLon, BigDecimal maxLon);

    /**
     * Find recent locations (updated within specified time)
     */
    List<UserLocation> findRecentLocations(Instant since);

    /**
     * Find stale locations (not updated since specified time)
     */
    List<UserLocation> findStaleLocations(Instant before);

    /**
     * Check if user has recent location data
     */
    boolean hasRecentLocation(UUID userId, Instant since);

    /**
     * Delete all locations for a user
     */
    void deleteByUserId(UUID userId);

    /**
     * Delete old locations (older than specified time)
     */
    void deleteOldLocations(Instant before);
}