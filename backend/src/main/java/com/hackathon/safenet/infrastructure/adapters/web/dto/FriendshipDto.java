package com.hackathon.safenet.infrastructure.adapters.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon.safenet.domain.model.Friendship;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for Friendship
 */
@Data
@Builder
@Schema(description = "Friendship information")
public class FriendshipDto {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the friendship", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @JsonProperty("user1Id")
    @Schema(description = "ID of the first user in the friendship", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID user1Id;

    @JsonProperty("user2Id")
    @Schema(description = "ID of the second user in the friendship", example = "123e4567-e89b-12d3-a456-426614174002")
    private UUID user2Id;

    @JsonProperty("createdAt")
    @Schema(description = "Timestamp when the friendship was created", example = "2024-01-15T10:30:00Z")
    private Instant createdAt;

    /**
     * Maps a domain {@link Friendship} to a {@link FriendshipDto}.
     *
     * @param friendship the domain friendship
     * @return the mapped FriendshipDto
     */
    public static FriendshipDto from(Friendship friendship) {
        if (friendship == null) {
            return null;
        }

        return FriendshipDto.builder()
                .id(friendship.id())
                .user1Id(friendship.user1Id())
                .user2Id(friendship.user1Id())
                .createdAt(friendship.createdAt())
                .build();
    }
}