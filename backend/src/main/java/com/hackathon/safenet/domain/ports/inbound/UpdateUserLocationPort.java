package com.hackathon.safenet.domain.ports.inbound;

import com.hackathon.safenet.domain.model.UserLocation;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Port interface for updating user location operations.
 * 
 * <p>This port defines the contract for location-related use cases,
 * providing methods for updating, retrieving, and managing user
 * location data with privacy controls.</p>
 * 
 * <h3>Key Operations</h3>
 * <ul>
 *   <li>Update user's current location</li>
 *   <li>Manage location visibility settings</li>
 *   <li>Retrieve current and friend locations</li>
 *   <li>Handle emergency location sharing</li>
 * </ul>
 * 
 * @author SafeNet Development Team
 * @since 1.0.0
 */
public interface UpdateUserLocationPort {
    
    /**
     * Update user's current location with coordinates and visibility settings.
     * 
     * <p>This method updates the user's location data and triggers
     * appropriate notifications to friends based on privacy settings.</p>
     * 
     * @param userId the unique identifier of the user
     * @param latitude the latitude coordinate (-90 to 90 degrees)
     * @param longitude the longitude coordinate (-180 to 180 degrees)
     * @param altitude the altitude in meters (optional, can be null)
     * @param accuracy the location accuracy in meters (optional, can be null)
     * @param visibleToFriends whether the location should be visible to friends
     * @return the updated UserLocation entity
     * @throws IllegalArgumentException if coordinates are invalid or user ID is null
     * @throws RuntimeException if user is not found
     */
    UserLocation updateLocation(UUID userId,
                                BigDecimal latitude,
                                BigDecimal longitude,
                                BigDecimal altitude,
                                BigDecimal accuracy,
                                Boolean visibleToFriends);
    
    /**
     * Update location visibility settings for a user.
     * 
     * <p>This method allows users to control whether their location
     * is shared with friends without updating the actual coordinates.</p>
     * 
     * @param userId the unique identifier of the user
     * @param visible whether the location should be visible to friends
     * @return the updated UserLocation entity
     * @throws IllegalArgumentException if user ID is null
     * @throws RuntimeException if user is not found
     */
    UserLocation updateLocationVisibility(UUID userId, Boolean visible);
    
    /**
     * Share emergency location with all friends regardless of privacy settings.
     * 
     * <p>This method bypasses normal privacy restrictions to share
     * location data during emergency situations. All friends will
     * receive the location update regardless of visibility settings.</p>
     * 
     * @param userId the unique identifier of the user in emergency
     * @param emergencyMessage optional message describing the emergency
     * @return the user's current location
     * @throws IllegalArgumentException if user ID is null
     * @throws RuntimeException if user is not found
     * @throws IllegalStateException if no location data is available
     */
    UserLocation shareEmergencyLocation(UUID userId, String emergencyMessage);
}