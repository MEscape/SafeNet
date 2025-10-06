package com.hackathon.safenet.domain.ports.inbound;

import com.hackathon.safenet.domain.model.User;

import java.util.Map;

public interface SyncUserPort {

    /**
     * Sync user from Keycloak event
     * Creates new user if not exists, updates if exists
     *
     * @param authId Keycloak user ID
     * @param username Username
     * @param email Email address
     * @param firstName First name
     * @param lastName Last name
     * @param attributes Additional Keycloak attributes
     * @return Synced user
     */
    User syncUser(String authId, String username, String email, String firstName, String lastName, Map<String, Object> attributes);

    /**
     * Handle user deletion event from Keycloak
     *
     * @param authId Keycloak user ID to delete
     */
    void deleteUser(String authId);
}
