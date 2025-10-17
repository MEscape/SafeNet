package com.hackathon.safenet.infrastructure.adapters.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon.safenet.domain.model.FriendRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for Friend Request
 */
@Data
@Builder
@Schema(description = "Friend request information")
public class FriendRequestDto {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the friend request", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @JsonProperty("requesterId")
    @Schema(description = "ID of the user who sent the request", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID requesterId;

    @JsonProperty("requestedId")
    @Schema(description = "ID of the user who received the request", example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID requestedId;

    @JsonProperty("status")
    @Schema(description = "Status of the friend request", example = "PENDING", allowableValues = {"PENDING", "ACCEPTED", "REJECTED"})
    private String status;

    @JsonProperty("createdAt")
    @Schema(description = "Timestamp when the request was created", example = "2024-01-15T10:30:00Z")
    private Instant createdAt;

    @JsonProperty("updatedAt")
    @Schema(description = "Timestamp when the request was last updated", example = "2024-01-15T10:35:00Z")
    private Instant updatedAt;

    /**
     * Static mapper method to convert a domain FriendRequest to FriendRequestDto
     *
     * @param friendRequest the domain FriendRequest object
     * @return FriendRequestDto
     */
    public static FriendRequestDto from(FriendRequest friendRequest) {
        return FriendRequestDto.builder()
                .id(friendRequest.id())
                .requesterId(friendRequest.requesterId())
                .requestedId(friendRequest.requestedId())
                .status(friendRequest.status().name())
                .createdAt(friendRequest.createdAt())
                .updatedAt(friendRequest.updatedAt())
                .build();
    }
}