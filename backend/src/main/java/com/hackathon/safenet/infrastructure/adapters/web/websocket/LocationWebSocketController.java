package com.hackathon.safenet.infrastructure.adapters.web.websocket;

import com.hackathon.safenet.domain.ports.inbound.UpdateUserLocationPort;
import com.hackathon.safenet.infrastructure.adapters.web.dto.UpdateLocationDto;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * WebSocket controller for real-time location updates.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Hidden
public class LocationWebSocketController {

    private final UpdateUserLocationPort updateUserLocationPort;

    @MessageMapping("/location/update")
    public void updateLocation(@Payload UpdateLocationDto locationUpdate,
                               Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        try {
            updateUserLocationPort.updateLocation(
                    userId,
                    locationUpdate.getLatitude(),
                    locationUpdate.getLongitude(),
                    locationUpdate.getAltitude(),
                    locationUpdate.getAccuracy(),
                    locationUpdate.getVisibleToFriends()
            );
        } catch (Exception e) {
            log.error("Failed to update location for user {}: {}", userId, e.getMessage(), e);
        }
    }

    @MessageMapping("/location/visibility")
    public void updateLocationVisibility(@Payload Map<String, Boolean> visibilityUpdate,
                                         Principal principal) {
        UUID userId = UUID.fromString(principal.getName());

        try {
            Boolean visible = visibilityUpdate.get("visible");
            if (visible == null) {
                throw new IllegalArgumentException("Visibility setting is required");
            }

            updateUserLocationPort.updateLocationVisibility(userId, visible);

        } catch (Exception e) {
            log.error("Failed to update visibility for user {}: {}", userId, e.getMessage(), e);
        }
    }

    @MessageMapping("/location/emergency")
    public void sendEmergencyAlert(@Payload Map<String, Object> emergencyData,
                                   Principal principal) {
        UUID userId = UUID.fromString(principal.getName());
        try {
            String message = (String) emergencyData.getOrDefault("message", "Emergency situation");
            updateUserLocationPort.shareEmergencyLocation(userId, message);
        } catch (Exception e) {
            log.error("Failed to send emergency alert for user {}: {}", userId, e.getMessage(), e);
        }
    }
}