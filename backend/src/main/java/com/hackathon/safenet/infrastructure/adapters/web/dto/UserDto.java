package com.hackathon.safenet.infrastructure.adapters.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon.safenet.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Data Transfer Object for User
 */
@Data
@Builder
@Schema(description = "User information")
public class UserDto {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the user", example = "123e4567-e89b-12d3-a456-426614174001")
    private UUID id;

    @JsonProperty("username")
    @Schema(description = "Username of the user", example = "john_doe")
    private String username;

    @JsonProperty("email")
    @Schema(description = "Email of the user", example = "john@example.com")
    private String email;

    @JsonProperty("firstName")
    @Schema(description = "First name of the user", example = "John")
    private String firstName;

    @JsonProperty("lastName")
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;

    /**
     * Maps a domain {@link User} to a {@link UserDto}.
     *
     * @param user the domain user
     * @return the mapped UserDto
     */
    public static UserDto from(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.id())
                .username(user.username())
                .email(user.email())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .build();
    }
}
