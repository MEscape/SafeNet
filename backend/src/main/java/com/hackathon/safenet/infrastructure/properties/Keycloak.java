package com.hackathon.safenet.infrastructure.properties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Keycloak {

    @NotNull
    private Webhook webhook = new Webhook();

    @Data
    public static class Webhook {
        // Basic Authentication configuration
        @NotBlank
        private String username = "admin";

        @NotBlank
        private String password = "password";
    }
}