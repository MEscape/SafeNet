package com.hackathon.safenet.application.config;

import com.hackathon.safenet.infrastructure.properties.ApplicationProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI Configuration
 * Configures Swagger UI and OpenAPI documentation using ApplicationProperties
 * 
 * <p>Access points:
 * <ul>
 *   <li>Swagger UI: http://localhost:{port}/swagger-ui.html</li>
 *   <li>OpenAPI JSON: http://localhost:{port}/v3/api-docs</li>
 * </ul>
 * 
 * <p>Production considerations:
 * <ul>
 *   <li>Swagger UI access is controlled via SecurityConfig</li>
 *   <li>Can be disabled in production by removing from permitAll() endpoints</li>
 *   <li>API versioning and examples can be added as the API evolves</li>
 *   <li>Server URLs are configurable via ApplicationProperties</li>
 * </ul>
 * 
 * @see ApplicationProperties for configuration options
 * @see SecurityConfig for access control
 */
@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final ApplicationProperties applicationProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + applicationProperties.getApp().getPort())
                                .description("Local Development Server"),
                        new Server()
                                .url(applicationProperties.getApp().getProductionServerUrl())
                                .description("Production Server")
                ))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearer-jwt")
                        .addList("webhook-signature"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("JWT token from Keycloak"))
                        .addSecuritySchemes("webhook-basic-auth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Basic Authentication for webhook endpoints"))
                );
    }

    private Info apiInfo() {
        return new Info()
                .title(applicationProperties.getApp().getName())
                .version(applicationProperties.getApp().getVersion())
                .description(applicationProperties.getApp().getDescription() + """
                
                ## Features
                - Hexagonal Architecture (Ports & Adapters)
                - OAuth2 Resource Server with JWT validation
                - Webhook endpoint for Keycloak events
                - PostgreSQL with JSONB support
                - Comprehensive test coverage
                
                ## Authentication
                Most endpoints require JWT authentication from Keycloak.
                Webhook endpoints use HMAC signature validation.
                
                ## Keycloak Configuration
                1. Create realm and configure event listener
                2. Configure event listener to POST to webhook endpoints
                3. Share webhook secret for HMAC validation
                """)
                .contact(new Contact()
                        .name(applicationProperties.getApp().getContact().getName())
                        .email(applicationProperties.getApp().getContact().getEmail())
                        .url(applicationProperties.getApp().getContact().getUrl()))
                .license(new License()
                        .name(applicationProperties.getApp().getLicense().getName())
                        .url(applicationProperties.getApp().getLicense().getUrl()));
    }
}