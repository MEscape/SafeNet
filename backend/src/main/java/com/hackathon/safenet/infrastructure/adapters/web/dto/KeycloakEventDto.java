package com.hackathon.safenet.infrastructure.adapters.web.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

/**
 * Data Transfer Object for Keycloak Event Webhook.
 * Captures known fields and also unknown fields for logging/debugging.
 */
@Data
public class KeycloakEventDto {

    @JsonProperty("type")
    private String type;  // REGISTER, LOGIN, USER-UPDATE, etc.

    @JsonProperty("realmId")
    private String realmId;

    private UUID userId;

    @JsonProperty("time")
    private Long time;

    @JsonProperty("details")
    private Map<String, Object> details;

    @Getter(AccessLevel.NONE)
    @JsonProperty("representation")
    private String representation;

    @JsonAnySetter
    public void setOtherField(String key, Object value) {
        System.out.println("All fields received: " + key + " = " + value);
    }

    public UUID getUserId() {
        Map<String, Object> detailsMap = getDetails();
        if (detailsMap != null && detailsMap.containsKey("id")) {
            try {
                return UUID.fromString(detailsMap.get("id").toString());
            } catch (Exception e) {
            }
        }
        return null;
    }

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
            }
        }
        return null;
    }

    /**
     * Check if this is a user registration or update event.
     */
    public boolean isUserSyncEvent() {
        return ("REGISTER".equals(type) || "USER-UPDATE".equals(type));
    }

    /**
     * Check if this is a user deletion event.
     */
    public boolean isUserDeleteEvent() {
        return "USER-DELETE".equals(type);
    }
}
