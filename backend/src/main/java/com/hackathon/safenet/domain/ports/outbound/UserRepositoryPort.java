package com.hackathon.safenet.domain.ports.outbound;

import com.hackathon.safenet.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {

    /**
     * Find user by Keycloak auth ID
     */
    Optional<User> findByAuthId(String authId);

    /**
     * Save or update user
     */
    User save(User user);

    /**
     * Delete user by auth ID
     */
    void deleteByAuthId(String authId);

    /**
     * Check if user exists
     */
    boolean existsByAuthId(String authId);
}