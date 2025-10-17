package com.hackathon.safenet.infrastructure.adapters.web.controller;

import com.hackathon.safenet.domain.model.UserLocation;
import com.hackathon.safenet.domain.ports.inbound.UserLocationPort;
import com.hackathon.safenet.infrastructure.adapters.web.dto.UserLocationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing user locations.
 * 
 * <p>This controller serves as the primary adapter in the hexagonal architecture,
 * handling HTTP requests for user location operations and delegating processing
 * to the appropriate use case services.</p>
 * 
 * <h3>Supported Operations</h3>
 * <ul>
 *   <li>Get Friends' Locations - Retrieve visible locations of friends</li>
 *   <li>Get Friend Location - Retrieve specific friend's location</li>
 * </ul>
 * 
 * <h3>Security Features</h3>
 * <ul>
 *   <li>JWT Authentication required for all endpoints</li>
 *   <li>User context extracted from authentication token</li>
 *   <li>Authorization checks for location access</li>
 * </ul>
 * 
 * @author SafeNet Development Team
 * @since 1.0.0
 * @see com.hackathon.safenet.domain.ports.inbound.UserLocationPort
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Tag(name = "User Locations", description = "User location management operations")
public class UserLocationController {

    private final UserLocationPort userLocationPort;

    /**
     * Get visible locations of the authenticated user's friends.
     */
    @GetMapping("/friends")
    @Operation(
        summary = "Get friends' locations",
        description = "Retrieve visible locations of the authenticated user's friends"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Friends' locations retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserLocationDto.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        )
    })
    public ResponseEntity<List<UserLocationDto>> getFriendsLocations(Authentication authentication) {
        
        log.debug("Getting friends' locations for user: {}", authentication.getName());
        
        UUID userId = UUID.fromString(authentication.getName());
        List<UserLocation> friendsLocations = userLocationPort.getFriendsLocations(userId);
        
        List<UserLocationDto> responseDtos = friendsLocations.stream()
                .map(UserLocationDto::from)
                .toList();
        
        log.info("Retrieved {} friends' locations for user: {}", responseDtos.size(), authentication.getName());
        
        return ResponseEntity.ok(responseDtos);
    }

    /**
     * Get a specific friend's location by friend ID.
     */
    @GetMapping("/friends/{friendId}")
    @Operation(
        summary = "Get specific friend's location",
        description = "Retrieve the location of a specific friend if visible and friendship exists"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Friend's location retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserLocationDto.class))
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Not authorized to view this friend's location"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Friend not found or location not visible"
        )
    })
    public ResponseEntity<UserLocationDto> getFriendLocation(
            @Parameter(description = "Friend's user ID", required = true)
            @PathVariable UUID friendId,
            Authentication authentication) {
        
        log.debug("Getting location for friend {} requested by user: {}", friendId, authentication.getName());
        
        UUID userId = UUID.fromString(authentication.getName());
        List<UserLocation> friendsLocations = userLocationPort.getFriendsLocations(userId);
        
        // Find the specific friend's location
        UserLocation friendLocation = friendsLocations.stream()
                .filter(location -> location.userId().equals(friendId))
                .findFirst()
                .orElse(null);
        
        if (friendLocation == null) {
            log.warn("Friend location not found or not visible: friendId={}, userId={}", friendId, userId);
            return ResponseEntity.notFound().build();
        }
        
        UserLocationDto responseDto = UserLocationDto.from(friendLocation);
        log.info("Friend location retrieved successfully: friendId={}, userId={}", friendId, userId);
        
        return ResponseEntity.ok(responseDto);
    }
}