package com.hackathon.safenet.infrastructure.adapters.web.controller;

import com.hackathon.safenet.domain.model.Friendship;
import com.hackathon.safenet.domain.model.User;
import com.hackathon.safenet.domain.ports.inbound.FriendshipPort;
import com.hackathon.safenet.infrastructure.adapters.web.dto.FriendCountDto;
import com.hackathon.safenet.infrastructure.adapters.web.dto.FriendshipDto;
import com.hackathon.safenet.infrastructure.adapters.web.dto.FriendshipStatusDto;
import com.hackathon.safenet.infrastructure.adapters.web.dto.UserDto;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing friendships.
 * 
 * <p>This controller serves as the primary adapter in the hexagonal architecture,
 * handling HTTP requests for friendship operations and delegating processing
 * to the appropriate use case services.</p>
 * 
 * <h3>Supported Operations</h3>
 * <ul>
 *   <li>Get Friends - Retrieve user's friends list</li>
 *   <li>Get Friendships - Retrieve user's friendships</li>
 *   <li>Check Friendship - Check if two users are friends</li>
 *   <li>Remove Friend - End a friendship</li>
 *   <li>Get Mutual Friends - Find mutual friends between users</li>
 *   <li>Get Friend Count - Get total number of friends</li>
 *   <li>Get Recent Friendships - Get recently formed friendships</li>
 * </ul>
 * 
 * <h3>Security Features</h3>
 * <ul>
 *   <li>JWT Authentication required for all endpoints</li>
 *   <li>User context extracted from authentication token</li>
 *   <li>Authorization checks for friendship access</li>
 * </ul>
 * 
 * @author SafeNet Development Team
 * @since 1.0.0
 * @see com.hackathon.safenet.domain.ports.inbound.FriendshipPort
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/friendships")
@RequiredArgsConstructor
@Tag(name = "Friendships", description = "Friendship management operations")
public class FriendshipController {

    private final FriendshipPort friendshipPort;

    /**
     * Get the current user's friends.
     * 
     * @param authentication the current user's authentication
     * @return list of friends
     */
    @Operation(summary = "Get friends", description = "Get the current user's friends list")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friends retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/friends")
    public ResponseEntity<List<UserDto>> getFriends(Authentication authentication) {
        log.info("Getting friends for user {}", authentication.getName());
        
        UUID userId = UUID.fromString(authentication.getName());
        List<User> friends = friendshipPort.getFriends(userId);
        
        List<UserDto> response = friends.stream()
                .map(UserDto::from)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get the current user's friendships.
     * 
     * @param authentication the current user's authentication
     * @return list of friendships
     */
    @Operation(summary = "Get friendships", description = "Get the current user's friendships")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friendships retrieved successfully",
                content = @Content(schema = @Schema(implementation = FriendshipDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<FriendshipDto>> getFriendships(Authentication authentication) {
        log.info("Getting friendships for user {}", authentication.getName());
        
        UUID userId = UUID.fromString(authentication.getName());
        List<Friendship> friendships = friendshipPort.getFriendships(userId);
        
        List<FriendshipDto> response = friendships.stream()
                .map(FriendshipDto::from)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if the current user is friends with another user.
     * 
     * @param userId the ID of the user to check friendship with
     * @param authentication the current user's authentication
     * @return friendship status
     */
    @Operation(summary = "Check friendship", description = "Check if the current user is friends with another user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friendship status retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/check/{userId}")
    public ResponseEntity<FriendshipStatusDto> checkFriendship(
            @Parameter(description = "ID of the user to check friendship with")
            @PathVariable UUID userId,
            Authentication authentication) {
        
        log.info("Checking friendship between user {} and user {}", authentication.getName(), userId);
        
        UUID currentUserId = UUID.fromString(authentication.getName());
        boolean areFriends = friendshipPort.areFriends(currentUserId, userId);
        
        FriendshipStatusDto response = FriendshipStatusDto.from(areFriends);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove a friend (end friendship).
     * 
     * @param friendId the ID of the friend to remove
     * @param authentication the current user's authentication
     * @return success response
     */
    @Operation(summary = "Remove friend", description = "Remove a friend (end friendship)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend removed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Friendship not found")
    })
    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(
            @Parameter(description = "ID of the friend to remove")
            @PathVariable UUID friendId,
            Authentication authentication) {
        
        log.info("User {} removing friend {}", authentication.getName(), friendId);
        
        UUID userId = UUID.fromString(authentication.getName());
        friendshipPort.removeFriendship(userId, friendId, userId);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Get mutual friends between the current user and another user.
     * 
     * @param userId the ID of the user to find mutual friends with
     * @param authentication the current user's authentication
     * @return list of mutual friends
     */
    @Operation(summary = "Get mutual friends", description = "Get mutual friends between the current user and another user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mutual friends retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/mutual/{userId}")
    public ResponseEntity<List<UserDto>> getMutualFriends(
            @Parameter(description = "ID of the user to find mutual friends with")
            @PathVariable UUID userId,
            Authentication authentication) {
        
        log.info("Getting mutual friends between user {} and user {}", authentication.getName(), userId);
        
        UUID currentUserId = UUID.fromString(authentication.getName());
        List<User> mutualFriends = friendshipPort.getMutualFriends(currentUserId, userId);
        
        List<UserDto> response = mutualFriends.stream()
                .map(UserDto::from)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get the current user's friend count.
     * 
     * @param authentication the current user's authentication
     * @return friend count
     */
    @Operation(summary = "Get friend count", description = "Get the current user's total number of friends")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend count retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/count")
    public ResponseEntity<FriendCountDto> getFriendCount(Authentication authentication) {
        log.info("Getting friend count for user {}", authentication.getName());
        
        UUID userId = UUID.fromString(authentication.getName());
        long count = friendshipPort.getFriendCount(userId);
        
        FriendCountDto response = FriendCountDto.from(count);
        return ResponseEntity.ok(response);
    }

    /**
     * Get recent friendships for the current user.
     * 
     * @param days number of days to look back (default: 30)
     * @param authentication the current user's authentication
     * @return list of recent friendships
     */
    @Operation(summary = "Get recent friendships", description = "Get recently formed friendships for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent friendships retrieved successfully",
                content = @Content(schema = @Schema(implementation = FriendshipDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/recent")
    public ResponseEntity<List<FriendshipDto>> getRecentFriendships(
            @Parameter(description = "Number of days to look back", example = "30")
            @RequestParam(defaultValue = "30") int days,
            Authentication authentication) {
        
        log.info("Getting recent friendships for user {} (last {} days)", authentication.getName(), days);
        
        UUID userId = UUID.fromString(authentication.getName());
        Instant since = Instant.now().minusSeconds(days * 24 * 60 * 60L);
        List<Friendship> recentFriendships = friendshipPort.getRecentFriendships(userId, since);
        
        List<FriendshipDto> response = recentFriendships.stream()
                .map(FriendshipDto::from)
                .toList();
        
        return ResponseEntity.ok(response);
    }
}