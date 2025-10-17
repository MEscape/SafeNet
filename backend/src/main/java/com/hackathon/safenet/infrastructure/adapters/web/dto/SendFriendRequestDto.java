package com.hackathon.safenet.infrastructure.adapters.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * Data Transfer Object for sending a friend request
 */
@Data
@Schema(description = "Request to send a friend request")
public class SendFriendRequestDto {

    @JsonProperty("requestedId")
    @NotNull(message = "Requested user ID is required")
    @Schema(description = "ID of the user to send the friend request to", example = "123e4567-e89b-12d3-a456-426614174002", required = true)
    private UUID requestedId;
}