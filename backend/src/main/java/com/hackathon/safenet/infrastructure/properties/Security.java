package com.hackathon.safenet.infrastructure.properties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Security {

    private boolean httpsOnly = false;

    private String[] corsAllowedOrigins = {
            "http://localhost:8081"
    };

    @NotNull
    private Jwt jwt = new Jwt();

    @Data
    public static class Jwt {
        @NotBlank
        private String issuerUri = "http://localhost:8080/realms/myrealm";

        private String audience;

        private long clockSkew = 60;
    }
}
