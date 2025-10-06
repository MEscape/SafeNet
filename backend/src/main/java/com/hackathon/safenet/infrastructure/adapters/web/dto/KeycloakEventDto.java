package com.hackathon.safenet.infrastructure.adapters.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

/**
 * Data Transfer Object for Keycloak Event Webhook
 * Keycloak event structure (simplified):
 * {
 *   "type": "REGISTER",
 *   "realmId": "my-realm",
 *   "userId": "abc-123",
 *   "time": 1234567890,
 *   "details": {
 *     "username": "john.doe",
 *     "email": "john@example.com",
 *     "first_name": "John",
 *     "last_name": "Doe"
 *   }
 * }
 */
@Data
public class KeycloakEventDto {

    @JsonProperty("type")
    private String type;  // REGISTER, LOGIN, USER-UPDATE, etc.

    @JsonProperty("realmId")
    private String realmId;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("time")
    private Long time;

    @JsonProperty("details")
    private Map<String, Object> details;

    @JsonProperty("representation")
    private String representation;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getDetails() {
        if (details != null) {
            return details;
        }

        if (representation != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(representation, Map.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Check if this is a user registration or update event
     */
    public boolean isUserSyncEvent() {
        return type != null && (
                type.equals("REGISTER") || type.equals("USER-UPDATE")
        );
    }

    /**
     * Check if this is a user deletion event
     */
    public boolean isUserDeleteEvent() {
        return type != null && type.equals("USER-DELETE");
    }
}