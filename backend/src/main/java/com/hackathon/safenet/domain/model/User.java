package com.hackathon.safenet.domain.model;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Immutable domain model representing an application user,
 * typically synchronized with Keycloak as the source of truth.
 */
public record User(
        UUID id,
        String authId,        // Keycloak user ID
        String username,
        String email,
        String firstName,
        String lastName,
        Map<String, Object> meta,  // JSONB metadata from Keycloak attributes
        Instant createdAt,
        Instant updatedAt
) {
    // Email pattern with max 255 characters constraint
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );
    private static final int EMAIL_MAX_LENGTH = 255;
    
    // Username pattern: 3-20 chars, letters, digits, dot, underscore, dash
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._-]{3,20}$"
    );
    
    // Name pattern: 1-50 chars, Unicode letters, spaces, apostrophes, dots, hyphens
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[\\p{L}\\s'.-]{1,50}$"
    );

    // Auth ID max length
    private static final int AUTH_ID_MAX_LENGTH = 255;

    /**
     * Factory method for creating a new user from Keycloak event
     *
     * @param authId Keycloak user ID
     * @param username Username from Keycloak
     * @param email Email from Keycloak
     * @param firstName First name from Keycloak
     * @param lastName Last name from Keycloak
     * @param meta Additional metadata (attributes, roles, etc.)
     * @return New User instance
     */
    public static User createFromKeycloak(
            String authId,
            String username,
            String email,
            String firstName,
            String lastName,
            Map<String, Object> meta) {
        Objects.requireNonNull(authId, "authId must not be null");

        Instant now = Instant.now();
        return new User(
                null,  // ID will be assigned by database
                authId,
                username,
                email,
                firstName,
                lastName,
                meta != null ? Map.copyOf(meta) : Map.of(),
                now,
                now
        );
    }

    /**
     * Update user with new data from Keycloak
     * Returns a new immutable instance with updated fields
     */
    public User updateFromKeycloak(
            String username,
            String email,
            String firstName,
            String lastName,
            Map<String, Object> meta) {

        return new User(
                this.id,
                this.authId,
                username,
                email,
                firstName,
                lastName,
                meta != null ? Map.copyOf(meta) : Map.of(),
                this.createdAt,
                Instant.now()  // Update timestamp
        );
    }

/**
     * Checks if the auth ID is valid.
     */
    public boolean hasValidAuthId() {
        return authId != null 
                && !authId.isBlank() 
                && authId.length() <= AUTH_ID_MAX_LENGTH;
    }

    /**
     * Checks if the email is syntactically valid and within length constraints.
     */
    public boolean hasValidEmail() {
        return email != null 
                && !email.isBlank()
                && email.length() <= EMAIL_MAX_LENGTH
                && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Checks if the username is valid.
     */
    public boolean hasValidUsername() {
        return username != null 
                && !username.isBlank()
                && USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Checks if the first name is valid (optional field).
     */
    public boolean hasValidFirstName() {
        return firstName == null 
                || firstName.isBlank() 
                || NAME_PATTERN.matcher(firstName.trim()).matches();
    }

    /**
     * Checks if the last name is valid (optional field).
     */
    public boolean hasValidLastName() {
        return lastName == null 
                || lastName.isBlank() 
                || NAME_PATTERN.matcher(lastName.trim()).matches();
    }

    /**
     * Business rule: User must have valid required fields.
     * Required: authId, username, email
     * Optional but must be valid if present: firstName, lastName
     */
    public boolean isValid() {
        return hasValidAuthId()
                && hasValidUsername() 
                && hasValidEmail() 
                && hasValidFirstName() 
                && hasValidLastName();
    }
}