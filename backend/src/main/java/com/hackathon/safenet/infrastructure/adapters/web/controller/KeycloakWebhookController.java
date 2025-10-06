package com.hackathon.safenet.infrastructure.adapters.web.controller;

import com.hackathon.safenet.domain.model.User;
import com.hackathon.safenet.domain.ports.inbound.SyncUserPort;
import com.hackathon.safenet.infrastructure.adapters.web.dto.KeycloakEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for handling Keycloak webhook events.
 * 
 * <p>This controller serves as the primary adapter in the hexagonal architecture,
 * receiving webhook events from Keycloak and delegating processing to the appropriate
 * use case services. It handles user lifecycle events such as creation, updates,
 * and deletions.</p>
 * 
 * <h3>Supported Events</h3>
 * <ul>
 *   <li>User Registration - Creates new user records</li>
 *   <li>User Profile Updates - Synchronizes user data changes</li>
 *   <li>User Deletion - Removes user records</li>
 * </ul>
 * 
 * <h3>Security Features</h3>
 * <ul>
 *   <li>Basic Authentication for webhook endpoints</li>
 *   <li>Spring Security integration for authentication</li>
 *   <li>Configurable username and password credentials</li>
 * </ul>
 * 
 * <h3>Keycloak Configuration</h3>
 * <p>To configure Keycloak to send events to this controller:</p>
 * <ol>
 *   <li>Navigate to Admin Console → Realm Settings → Events → Event Listeners</li>
 *   <li>Add custom event listener that POSTs to {@code /api/v1/hooks/keycloak/user-event}</li>
 *   <li>Configure Basic Authentication with username and password</li>
 *   <li>Ensure the {@code Authorization} header with Basic credentials is included in requests</li>
 * </ol>
 * 
 * <h3>Error Handling</h3>
 * <p>The controller provides comprehensive error handling for:</p>
 * <ul>
 *   <li>Invalid event formats</li>
 *   <li>Missing required fields</li>
 *   <li>Database synchronization failures</li>
 *   <li>Authentication and authorization errors</li>
 * </ul>
 * 
 * @author SafeNet Development Team
 * @since 1.0.0
 * @see com.hackathon.safenet.domain.ports.inbound.SyncUserPort
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/hooks/keycloak")
@RequiredArgsConstructor
public class KeycloakWebhookController {

    private final SyncUserPort syncUserPort;

    /**
     * Handles Keycloak user lifecycle events via webhook.
     * 
     * <p>This endpoint receives POST requests from Keycloak when user events occur.
     * It processes user registration, profile updates, and deletion events to keep
     * the local database synchronized with Keycloak's user store.</p>
     * 
     * <h4>Supported Event Types</h4>
     * <ul>
     *   <li>{@code REGISTER} - New user registration</li>
     *   <li>{@code UPDATE_PROFILE} - User profile changes</li>
     *   <li>{@code DELETE_ACCOUNT} - User account deletion</li>
     * </ul>
     * 
     * <h4>Security</h4>
     * <p>Requests are authenticated using Basic Authentication. The username and password
     * must be provided in the {@code Authorization} header using Basic authentication scheme.</p>
     * 
     * <h4>Request Format</h4>
     * <pre>{@code
     * {
     *   "type": "REGISTER",
     *   "userId": "keycloak-user-id",
     *   "details": {
     *     "username": "john.doe",
     *     "email": "john.doe@example.com",
     *     "firstName": "John",
     *     "lastName": "Doe"
     *   }
     * }
     * }</pre>
     * 
     * @param event the Keycloak event data containing user information
     * @return response indicating the processing result
     * @throws IllegalArgumentException if the event data is invalid
     * @throws RuntimeException if database synchronization fails
     */
    @PostMapping({"/user-event", "/user-event/"})
    public ResponseEntity<Map<String, Object>> handleUserEvent(@RequestBody KeycloakEventDto event) {
        log.info("Received Keycloak event: type={}, userId={}", event.getType(), event.getUserId());

        if (event.isUserSyncEvent()) {
            return handleSyncEvent(event);
        } else if (event.isUserDeleteEvent()) {
            return handleDeleteEvent(event);
        } else {
            log.debug("Ignoring event type: {}", event.getType());
            return ResponseEntity.ok(Map.of(
                    "status", "ignored",
                    "message", "Event type not handled: " + event.getType()
            ));
        }
    }

    /**
     * Processes user synchronization events from Keycloak.
     * 
     * <p>This method handles user registration and profile update events by
     * extracting user data from the event and delegating to the sync service.
     * It creates or updates user records in the local database.</p>
     * 
     * @param event the Keycloak event containing user data
     * @return response with synchronization result and user information
     * @throws IllegalArgumentException if required user data is missing
     * @throws RuntimeException if database operation fails
     */
    private ResponseEntity<Map<String, Object>> handleSyncEvent(KeycloakEventDto event) {
        Map<String, Object> details = event.getDetails();

        String username = (String) details.get("username");
        String email = (String) details.get("email");
        String firstName = (String) details.getOrDefault("first_name", details.get("firstName"));
        String lastName = (String) details.getOrDefault("last_name", details.get("lastName"));
        
        // Extract additional attributes for JSONB meta field
        Map<String, Object> attributes = new HashMap<>(details);
        attributes.remove("username");
        attributes.remove("email");
        attributes.remove("first_name");
        attributes.remove("firstName");
        attributes.remove("last_name");
        attributes.remove("lastName");

        User syncedUser = syncUserPort.syncUser(
                event.getUserId(),
                username,
                email,
                firstName,
                lastName,
                attributes
        );

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "action", "synced",
                "userId", syncedUser.id().toString(),
                "authId", syncedUser.authId()
        ));
    }

    /**
     * Processes user deletion events from Keycloak.
     * 
     * <p>This method handles user account deletion events by removing the
     * corresponding user record from the local database. It ensures data
     * consistency between Keycloak and the local user store.</p>
     * 
     * @param event the Keycloak deletion event containing user identifier
     * @return response confirming the deletion operation
     * @throws RuntimeException if database deletion fails
     */
    private ResponseEntity<Map<String, Object>> handleDeleteEvent(KeycloakEventDto event) {
        syncUserPort.deleteUser(event.getUserId());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "action", "deleted",
                "authId", event.getUserId()
        ));
    }
}