package com.hackathon.safenet.application.config;

import com.hackathon.safenet.infrastructure.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * JWT and OAuth2 Resource Server security configuration for the SafeNet application.
 *
 * <p>This configuration handles JWT-based authentication for the main application endpoints.
 * It configures the application as an OAuth2 Resource Server that validates JWT tokens
 * issued by Keycloak.</p>
 *
 * <h3>JWT Configuration Features</h3>
 * <ul>
 *   <li><strong>JWT Validation:</strong> Validates JWT tokens using Keycloak's public keys</li>
 *   <li><strong>Authority Mapping:</strong> Maps JWT claims to Spring Security authorities</li>
 *   <li><strong>Stateless Sessions:</strong> No server-side session storage</li>
 *   <li><strong>Security Headers:</strong> HSTS, Content-Type, Frame Options, Referrer Policy</li>
 *   <li><strong>HTTPS Enforcement:</strong> Configurable redirect to secure connections</li>
 * </ul>
 *
 * <h3>Authority Mapping</h3>
 * <p>The configuration maps JWT claims to Spring Security authorities:</p>
 * <ul>
 *   <li><strong>Realm Roles:</strong> Mapped from 'realm_access.roles' claim</li>
 *   <li><strong>Resource Roles:</strong> Mapped from 'resource_access.{client}.roles' claim</li>
 *   <li><strong>Scope:</strong> Mapped from 'scope' claim</li>
 * </ul>
 *
 * <h3>Public Endpoints</h3>
 * <p>The following endpoints are accessible without authentication:</p>
 * <ul>
 *   <li>/actuator/health - Health check endpoint</li>
 *   <li>/api/v1/public/** - Public API endpoints</li>
 *   <li>/swagger-ui/** - API documentation</li>
 *   <li>/v3/api-docs/** - OpenAPI specification</li>
 * </ul>
 *
 */
@Configuration
@RequiredArgsConstructor
public class JwtSecurityConfig {

    private final ApplicationProperties applicationProperties;
    private final CorsSecurityConfig corsSecurityConfig;

    /**
     * Configures the security filter chain for JWT-based authentication.
     *
     * <p>This filter chain handles all non-webhook endpoints with JWT authentication.
     * It has lower precedence (order 2) and processes requests that don't match
     * the webhook filter chain.</p>
     *
     * @param http the HttpSecurity configuration object
     * @return the configured SecurityFilterChain for JWT authentication
     * @throws Exception if security configuration fails
     */
    @Bean
    @Order(2)
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        return http
                // Apply to all requests except webhook endpoints
                .securityMatcher(request -> 
                    !request.getRequestURI().startsWith("/api/v1/hooks/keycloak"))
                
                // Disable CSRF for stateless API
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configure CORS
                .cors(cors -> cors.configurationSource(corsSecurityConfig.corsConfigurationSource()))
                
                // Configure session management
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure security headers
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                        .contentTypeOptions(contentTypeOptions -> {})
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true))
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                )

                // Configure HTTPS enforcement if enabled
                .requiresChannel(channel -> {
                    if (applicationProperties.getSecurity().isHttpsOnly()) {
                        channel.anyRequest().requiresSecure();
                    }
                })
                
                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                    // Public endpoints
                    .requestMatchers("/actuator/health").permitAll()
                    .requestMatchers("/api/v1/public/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("/error").permitAll()
                    
                    // All other endpoints require authentication
                    .anyRequest().authenticated()
                )
                
                // Configure OAuth2 Resource Server with JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                        .decoder(jwtDecoder())
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                    )
                )
                
                .build();
    }

    /**
     * Configures the JWT decoder for validating JWT tokens.
     *
     * <p>The decoder is configured to use Keycloak's JWK Set URI to fetch
     * public keys for JWT signature validation.</p>
     *
     * @return the configured JwtDecoder
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        String issuerUri = applicationProperties.getSecurity().getJwt().getIssuerUri();
        String jwkSetUri = issuerUri + "/protocol/openid_connect/certs";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    /**
     * Configures the JWT authentication converter for mapping JWT claims to authorities.
     *
     * <p>This converter extracts authorities from JWT claims and maps them to
     * Spring Security authorities. It handles both realm roles and resource roles
     * from Keycloak JWT tokens.</p>
     *
     * @return the configured JwtAuthenticationConverter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        
        // Configure authority prefix and claim names
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("realm_access.roles");
        
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        
        return authenticationConverter;
    }
}