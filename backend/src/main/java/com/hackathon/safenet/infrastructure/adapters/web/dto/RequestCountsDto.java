package com.hackathon.safenet.infrastructure.adapters.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for returning counts of pending friend requests.
 */
@Data
@Builder
@Schema(description = "Friend request counts")
public class RequestCountsDto {

    @Schema(description = "Number of pending received requests", example = "3")
    private long receivedCount;

    @Schema(description = "Number of pending sent requests", example = "1")
    private long sentCount;

    /**
     * Static mapper for convenience.
     *
     * @param receivedCount number of received requests
     * @param sentCount number of sent requests
     * @return RequestCountsDto
     */
    public static RequestCountsDto from(long receivedCount, long sentCount) {
        return RequestCountsDto.builder()
                .receivedCount(receivedCount)
                .sentCount(sentCount)
                .build();
    }
}
