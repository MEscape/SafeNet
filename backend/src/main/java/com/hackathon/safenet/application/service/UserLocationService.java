package com.hackathon.safenet.application.service;

import com.hackathon.safenet.domain.model.User;
import com.hackathon.safenet.domain.model.UserLocation;
import com.hackathon.safenet.domain.ports.inbound.UserLocationPort;
import com.hackathon.safenet.domain.ports.outbound.FriendshipRepositoryPort;
import com.hackathon.safenet.domain.ports.outbound.UserLocationRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service implementation responsible for managing and retrieving user location data.
 *
 * <p>This service provides functionality for accessing the latest locations
 * of a user's friends, considering their visibility preferences. It acts as
 * the main application layer between domain logic and persistence adapters
 * for user location tracking and sharing.</p>
 *
 * <p>It supports real-time location sharing scenarios where users can
 * control visibility and access to their live positions. The service
 * ensures efficient data retrieval and filtering of visible friend
 * locations.</p>
 *
 * <p>Transactional boundaries are applied to ensure consistency and
 * performance for read operations.</p>
 *
 * @author SafeNet Development Team
 * @since 1.0.0
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserLocationService implements UserLocationPort {

    private final UserLocationRepositoryPort userLocationRepository;
    private final FriendshipRepositoryPort friendshipRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserLocation> getFriendsLocations(UUID userId) {
        log.debug("Getting friends' locations for user {}", userId);
        
        // Get all friend IDs
        List<UUID> friendIds = friendshipRepository.findFriendsByUserId(userId).stream()
                .map(User::id)
                .toList();
        
        if (friendIds.isEmpty()) {
            return List.of();
        }
        
        // Get visible locations for friends
        return userLocationRepository.findLatestVisibleLocationsByUserIds(friendIds);
    }
}