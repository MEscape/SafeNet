package com.hackathon.safenet.domain.ports.outbound;

import java.util.Map;

public interface KeycloakEventPort {

    /**
     * Validate webhook signature or token
     *
     * @param payload Raw webhook payload
     * @param signature Signature header value
     * @return true if valid
     */
    boolean validateWebhook(String payload, String signature);

    /**
     * Extract user data from Keycloak event
     *
     * @param eventData Event JSON data
     * @return Extracted user attributes
     */
    Map<String, Object> extractUserAttributes(Map<String, Object> eventData);
}
