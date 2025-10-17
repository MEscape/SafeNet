package com.hackathon.safenet.infrastructure.adapters.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon.safenet.domain.model.ninapolice.NinaPoliceResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for NinaPoliceResponse
 */
@Data
@Builder
@Schema(description = "NINA police alerts response")
public class NinaPoliceResponseDto {

    @JsonProperty("items")
    @Schema(description = "List of police alert items")
    private List<NinaPoliceItemDto> items;

    /**
     * Maps a domain {@link NinaPoliceResponse} to a {@link NinaPoliceResponseDto}.
     *
     * @param response the domain police response
     * @return the mapped NinaPoliceResponseDto
     */
    public static NinaPoliceResponseDto from(NinaPoliceResponse response) {
        if (response == null) {
            return null;
        }

        return NinaPoliceResponseDto.builder()
                .items(response.getItems() != null ? 
                    response.getItems().stream()
                        .map(NinaPoliceItemDto::from)
                        .collect(Collectors.toList()) : null)
                .build();
    }
}