package com.hackathon.safenet.infrastructure.adapters.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO representing a user's friend count
 */
@Data
@AllArgsConstructor
@Schema(description = "Total number of friends for a user")
public class FriendCountDto {

    @Schema(description = "Number of friends", example = "42")
    private long count;

    /** Mapper from long (domain) */
    public static FriendCountDto from(long count) {
        return new FriendCountDto(count);
    }
}
