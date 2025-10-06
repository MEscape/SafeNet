package com.hackathon.safenet.infrastructure.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class App {

    @NotBlank
    private String name = "SafeNet";

    @NotBlank
    private String version = "1.0.0";

    private String description = "User synchronization service with Keycloak";

    @Positive
    private int port = 8080;

    private String productionServerUrl = "https://api.example.com";

    @NotNull
    private Contact contact = new Contact();

    @NotNull
    private License license = new License();

    @Data
    public static class Contact {
        private String name = "DevOps Team";
        private String email = "devops@example.com";
        private String url = "https://github.com/example/user-sync";
    }

    @Data
    public static class License {
        private String name = "Apache 2.0";
        private String url = "https://www.apache.org/licenses/LICENSE-2.0.html";
    }
}
