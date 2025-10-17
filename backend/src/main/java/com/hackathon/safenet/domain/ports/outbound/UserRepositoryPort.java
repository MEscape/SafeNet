package com.hackathon.safenet.domain.ports.outbound;

import com.hackathon.safenet.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for user persistence operations.
 *
 * <p>This interface defines the persistence operations available
 * for managing {@link User} entities in external storage.</p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Find, save, and delete users</li>
 *   <li>Check existence by both UUID (primary key) and Keycloak id</li>
 * </ul>
 *
 * <p>Originally based on Keycloak authentication ID, now extended
 * to support UUID-based lookups for domain-level consistency.</p>
 */
public interface UserRepositoryPort {

    /**
     * Find user by Keycloak auth ID.
     */
    Optional<User> findById(UUID id);

    /**
     * Save or update user.
     */
    User save(User user);

    /**
     * Delete user by Keycloak auth ID.
     */
    void deleteById(UUID id);

    /**
     * Check if user exists by Keycloak auth ID.
     */
    boolean existsById(UUID id);
}
