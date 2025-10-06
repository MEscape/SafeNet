package com.hackathon.safenet.infrastructure.adapters.supabase.repository;

import com.hackathon.safenet.infrastructure.adapters.supabase.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link UserEntity}.
 * <p>
 * This interface extends {@link JpaRepository} providing basic CRUD operations,
 * as well as custom query methods for finding, checking existence, and deleting users
 * by their authentication ID.
 * <p>
 * Spring Data JPA automatically generates the implementation at runtime.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Retrieves a {@link UserEntity} by its authentication ID.
     *
     * @param authId the authentication ID of the user
     * @return an {@link Optional} containing the {@link UserEntity} if found, or empty if not found
     */
    Optional<UserEntity> findByAuthId(String authId);

    /**
     * Checks whether a {@link UserEntity} exists with the given authentication ID.
     *
     * @param authId the authentication ID to check
     * @return {@code true} if a user with the given authId exists, {@code false} otherwise
     */
    boolean existsByAuthId(String authId);

    /**
     * Deletes a {@link UserEntity} by its authentication ID.
     *
     * @param authId the authentication ID of the user to delete
     */
    void deleteByAuthId(String authId);
}
