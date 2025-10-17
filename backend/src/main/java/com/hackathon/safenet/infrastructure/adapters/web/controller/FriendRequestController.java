package com.hackathon.safenet.infrastructure.adapters.web.controller;

import com.hackathon.safenet.domain.model.FriendRequest;
import com.hackathon.safenet.domain.ports.inbound.FriendRequestPort;
import com.hackathon.safenet.infrastructure.adapters.web.dto.FriendRequestDto;
import com.hackathon.safenet.infrastructure.adapters.web.dto.RequestCountsDto;
import com.hackathon.safenet.infrastructure.adapters.web.dto.SendFriendRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing friend requests.
 * 
 * <p>This controller serves as the primary adapter in the hexagonal architecture,
 * handling HTTP requests for friend request operations and delegating processing
 * to the appropriate use case services.</p>
 * 
 * <h3>Supported Operations</h3>
 * <ul>
 *   <li>Send Friend Request - Create a new friend request</li>
 *   <li>Accept Friend Request - Accept a pending friend request</li>
 *   <li>Reject Friend Request - Reject a pending friend request</li>
 *   <li>Cancel Friend Request - Cancel a sent friend request</li>
 *   <li>List Received Requests - Get pending friend requests received</li>
 *   <li>List Sent Requests - Get pending friend requests sent</li>
 *   <li>Get Request Counts - Get counts of pending requests</li>
 * </ul>
 * 
 * <h3>Security Features</h3>
 * <ul>
 *   <li>JWT Authentication required for all endpoints</li>
 *   <li>User context extracted from authentication token</li>
 *   <li>Authorization checks for request ownership</li>
 * </ul>
 * 
 * @author SafeNet Development Team
 * @since 1.0.0
 * @see com.hackathon.safenet.domain.ports.inbound.FriendRequestPort
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/friend-requests")
@RequiredArgsConstructor
@Tag(name = "Friend Requests", description = "Friend request management operations")
public class FriendRequestController {

    private final FriendRequestPort friendRequestPort;

    /**
     * Send a friend request to another user.
     * 
     * @param request the friend request data
     * @param authentication the current user's authentication
     * @return the created friend request
     */
    @Operation(summary = "Send a friend request", description = "Send a friend request to another user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Friend request sent successfully",
                content = @Content(schema = @Schema(implementation = FriendRequestDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Requested user not found"),
        @ApiResponse(responseCode = "409", description = "Friend request already exists or users are already friends")
    })
    @PostMapping
    public ResponseEntity<FriendRequestDto> sendFriendRequest(
            @Valid @RequestBody SendFriendRequestDto request,
            Authentication authentication) {
        
        log.info("Sending friend request from user {} to user {}", 
                authentication.getName(), request.getRequestedId());
        
        UUID requesterId = UUID.fromString(authentication.getName());
        FriendRequest friendRequest = friendRequestPort.sendFriendRequest(
                requesterId, request.getRequestedId());

        FriendRequestDto response = FriendRequestDto.from(friendRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Accept a friend request.
     * 
     * @param requestId the ID of the friend request to accept
     * @param authentication the current user's authentication
     * @return success response
     */
    @Operation(summary = "Accept a friend request", description = "Accept a pending friend request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend request accepted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Friend request not found"),
        @ApiResponse(responseCode = "409", description = "Friend request is not in pending status")
    })
    @PostMapping("/{requestId}/accept")
    public ResponseEntity<Void> acceptFriendRequest(
            @Parameter(description = "ID of the friend request to accept")
            @PathVariable UUID requestId,
            Authentication authentication) {
        
        log.info("User {} accepting friend request {}", authentication.getName(), requestId);
        
        UUID userId = UUID.fromString(authentication.getName());
        friendRequestPort.acceptFriendRequest(requestId, userId);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Reject a friend request.
     * 
     * @param requestId the ID of the friend request to reject
     * @param authentication the current user's authentication
     * @return success response
     */
    @Operation(summary = "Reject a friend request", description = "Reject a pending friend request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend request rejected successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Friend request not found"),
        @ApiResponse(responseCode = "409", description = "Friend request is not in pending status")
    })
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<Void> rejectFriendRequest(
            @Parameter(description = "ID of the friend request to reject")
            @PathVariable UUID requestId,
            Authentication authentication) {
        
        log.info("User {} rejecting friend request {}", authentication.getName(), requestId);
        
        UUID userId = UUID.fromString(authentication.getName());
        friendRequestPort.rejectFriendRequest(requestId, userId);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Cancel a sent friend request.
     * 
     * @param requestId the ID of the friend request to cancel
     * @param authentication the current user's authentication
     * @return success response
     */
    @Operation(summary = "Cancel a friend request", description = "Cancel a sent friend request")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend request cancelled successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Friend request not found"),
        @ApiResponse(responseCode = "409", description = "Friend request is not in pending status")
    })
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> cancelFriendRequest(
            @Parameter(description = "ID of the friend request to cancel")
            @PathVariable UUID requestId,
            Authentication authentication) {
        
        log.info("User {} cancelling friend request {}", authentication.getName(), requestId);
        
        UUID userId = UUID.fromString(authentication.getName());
        friendRequestPort.cancelFriendRequest(requestId, userId);
        
        return ResponseEntity.ok().build();
    }

    /**
     * Get pending friend requests received by the current user.
     * 
     * @param authentication the current user's authentication
     * @return list of received friend requests
     */
    @Operation(summary = "Get received friend requests", description = "Get pending friend requests received by the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend requests retrieved successfully",
                content = @Content(schema = @Schema(implementation = FriendRequestDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/received")
    public ResponseEntity<List<FriendRequestDto>> getReceivedFriendRequests(Authentication authentication) {
        log.info("Getting received friend requests for user {}", authentication.getName());
        
        UUID userId = UUID.fromString(authentication.getName());
        List<FriendRequest> requests = friendRequestPort.getPendingReceivedRequests(userId);
        
        List<FriendRequestDto> response = requests.stream()
                .map(FriendRequestDto::from)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get pending friend requests sent by the current user.
     * 
     * @param authentication the current user's authentication
     * @return list of sent friend requests
     */
    @Operation(summary = "Get sent friend requests", description = "Get pending friend requests sent by the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Friend requests retrieved successfully",
                content = @Content(schema = @Schema(implementation = FriendRequestDto.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/sent")
    public ResponseEntity<List<FriendRequestDto>> getSentFriendRequests(Authentication authentication) {
        log.info("Getting sent friend requests for user {}", authentication.getName());
        
        UUID userId = UUID.fromString(authentication.getName());
        List<FriendRequest> requests = friendRequestPort.getPendingSentRequests(userId);
        
        List<FriendRequestDto> response = requests.stream()
                .map(FriendRequestDto::from)
                .toList();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get counts of pending friend requests.
     * 
     * @param authentication the current user's authentication
     * @return counts of received and sent requests
     */
    @Operation(summary = "Get friend request counts", description = "Get counts of pending received and sent friend requests")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request counts retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/counts")
    public ResponseEntity<RequestCountsDto> getFriendRequestCounts(Authentication authentication) {
        log.info("Getting friend request counts for user {}", authentication.getName());

        UUID userId = UUID.fromString(authentication.getName());
        long receivedCount = friendRequestPort.getPendingReceivedRequestsCount(userId);
        long sentCount = friendRequestPort.getPendingSentRequestsCount(userId);

        return ResponseEntity.ok(RequestCountsDto.from(receivedCount, sentCount));
    }
}