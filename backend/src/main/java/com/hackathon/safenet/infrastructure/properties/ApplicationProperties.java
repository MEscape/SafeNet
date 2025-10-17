package com.hackathon.safenet.infrastructure.properties;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "safenet")
public class ApplicationProperties {

    @NotNull
    private Security security = new Security();

    @NotNull
    private Keycloak keycloak = new Keycloak();

    @NotNull
    private SpringDoc springDoc = new SpringDoc();
}
