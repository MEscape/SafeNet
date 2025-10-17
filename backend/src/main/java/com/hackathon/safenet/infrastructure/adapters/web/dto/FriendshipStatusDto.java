package com.hackathon.safenet.infrastructure.adapters.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing friendship status between two users
 */
@Data
@AllArgsConstructor
@Schema(description = "Friendship status between two users")
public class FriendshipStatusDto {

    @Schema(description = "Whether the users are friends", example = "true")
    private boolean areFriends;

    /** Mapper from boolean (domain) */
    public static FriendshipStatusDto from(boolean areFriends) {
        return new FriendshipStatusDto(areFriends);
    }
}
