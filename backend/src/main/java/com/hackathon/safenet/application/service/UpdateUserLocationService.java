package com.hackathon.safenet.application.service;

import com.hackathon.safenet.domain.model.UserLocation;
import com.hackathon.safenet.domain.model.User;
import com.hackathon.safenet.domain.model.Friendship;
import com.hackathon.safenet.domain.ports.inbound.UpdateUserLocationPort;
import com.hackathon.safenet.domain.ports.outbound.FriendshipRepositoryPort;
import com.hackathon.safenet.domain.ports.outbound.NotificationPort;
import com.hackathon.safenet.domain.model.NotificationMessage;
import com.hackathon.safenet.domain.enums.NotificationType;
import com.hackathon.safenet.domain.ports.outbound.UserLocationRepositoryPort;
import com.hackathon.safenet.domain.ports.outbound.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for managing user location updates with notifications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UpdateUserLocationService implements UpdateUserLocationPort {

    private final UserLocationRepositoryPort userLocationRepository;
    private final UserRepositoryPort userRepository;
    private final FriendshipRepositoryPort friendshipRepository;
    private final NotificationPort notificationPort;

    @Override
    public UserLocation updateLocation(UUID userId, BigDecimal latitude, BigDecimal longitude,
                                       BigDecimal altitude, BigDecimal accuracy, Boolean visibleToFriends) {
        log.debug("Updating location for user {}", userId);

        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found: " + userId);
        }

        // Domain model handles coordinate validation
        UserLocation location = userLocationRepository.findLatestByUserId(userId)
                .map(existing -> existing.updateCoordinates(latitude, longitude, altitude, accuracy))
                .orElseGet(() -> UserLocation.create(userId, latitude, longitude, altitude, accuracy,
                        visibleToFriends != null ? visibleToFriends : true));

        if (visibleToFriends != null && !visibleToFriends.equals(location.visibleToFriends())) {
            location = location.updateVisibility(visibleToFriends);
        }

        UserLocation saved = userLocationRepository.save(location);
        log.info("Location updated for user {}", userId);

        if (saved.visibleToFriends()) {
            notifyFriendsLocationUpdate(saved);
        }

        return saved;
    }

    @Override
    public UserLocation updateLocationVisibility(UUID userId, Boolean visible) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found: " + userId);
        }

        UserLocation location = userLocationRepository.findLatestByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("User has no location data"));

        UserLocation updated = location.updateVisibility(visible);
        UserLocation saved = userLocationRepository.save(updated);
        log.info("Location visibility updated for user {}", userId);

        notifyFriendsVisibilityChange(saved, visible);

        return saved;
    }

    @Override
    public UserLocation shareEmergencyLocation(UUID userId, String emergencyMessage) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found: " + userId);
        }

        UserLocation location = userLocationRepository.findLatestByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("No location data available"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        List<Friendship> friendships = friendshipRepository.findFriendshipsByUserId(userId);

        for (Friendship friendship : friendships) {
            UUID friendId = friendship.getOtherUserId(userId);
            if (friendId == null) continue;

            sendEmergencyNotification(friendId, user.username(), location, emergencyMessage);
        }

        log.info("Emergency location shared for user {} to {} friends", userId, friendships.size());

        return location;
    }

    private void notifyFriendsLocationUpdate(UserLocation location) {
        List<UUID> friendIds = friendshipRepository.findFriendsByUserId(location.userId())
                .stream().map(User::id).toList();

        Map<String, Object> data = Map.of(
                "userId", location.userId().toString(),
                "latitude", location.latitude(),
                "longitude", location.longitude(),
                "altitude", location.altitude(),
                "accuracy", location.accuracy(),
                "visible", location.visibleToFriends()
        );

        for (UUID friendId : friendIds) {
            NotificationMessage notification = NotificationMessage.create(
                    NotificationType.LOCATION_UPDATE,
                    location.userId().toString(),
                    friendId.toString(),
                    data
            );
            notificationPort.send(notification);
        }
    }

    private void notifyFriendsVisibilityChange(UserLocation location, boolean visible) {
        List<UUID> friendIds = friendshipRepository.findFriendsByUserId(location.userId())
                .stream().map(User::id).toList();

        Map<String, Object> data = Map.of(
                "userId", location.userId().toString(),
                "latitude", location.latitude(),
                "longitude", location.longitude(),
                "altitude", location.altitude(),
                "accuracy", location.accuracy(),
                "visible", visible
        );

        NotificationType type = visible ? NotificationType.LOCATION_SHARING_ENABLED
                : NotificationType.LOCATION_SHARING_DISABLED;

        for (UUID friendId : friendIds) {
            NotificationMessage notification = NotificationMessage.create(
                    type,
                    location.userId().toString(),
                    friendId.toString(),
                    data
            );
            notificationPort.send(notification);
        }
    }

    private void sendEmergencyNotification(UUID friendId, String userName,
                                           UserLocation location, String message) {
        NotificationMessage notification = NotificationMessage.create(
                NotificationType.EMERGENCY_ALERT,
                userName,
                friendId.toString(),
                Map.of(
                        "latitude", location.latitude(),
                        "longitude", location.longitude(),
                        "message", message != null ? message : "Emergency situation"
                )
        );
        notificationPort.send(notification);
    }
}