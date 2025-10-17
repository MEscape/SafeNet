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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

import java.util.*;

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
public class OpaqueTokenSecurityConfig {

    private final ApplicationProperties applicationProperties;
    private final CorsSecurityConfig corsSecurityConfig;
    private final OpaqueTokenIntrospector delegate;


    /**
     * Configures the security filter chain for opaque token-based authentication.
     *
     * <p>This filter chain handles all non-webhook endpoints with token introspection.
     * It has lower precedence (order 2) and processes requests that don't match
     * the webhook filter chain.</p>
     *
     * @param http the HttpSecurity configuration object
     * @return the configured SecurityFilterChain for opaque token authentication
     * @throws Exception if security configuration fails
     */
    @Bean
    @Order(2)
    public SecurityFilterChain opaqueTokenFilterChain(HttpSecurity http) throws Exception {
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

                // Configure OAuth2 Resource Server with opaque token introspection
                .oauth2ResourceServer(oauth2 -> oauth2
                        .opaqueToken(token -> token
                                .introspector(t -> {
                                    OAuth2AuthenticatedPrincipal principal = delegate.introspect(t);
                                    Map<String, Object> claims = principal.getAttributes();
                                    String principalName = (String) claims.get("sub");

                                    List<GrantedAuthority> authorities = new ArrayList<>();

                                    // Extract realm roles from realm_access.roles
                                    Object realmAccess = claims.get("realm_access");
                                    if (realmAccess instanceof Map) {
                                        @SuppressWarnings("unchecked")
                                        Map<String, Object> realmAccessMap = (Map<String, Object>) realmAccess;
                                        Object rolesObj = realmAccessMap.get("roles");

                                        if (rolesObj instanceof Collection) {
                                            @SuppressWarnings("unchecked")
                                            Collection<String> roles = (Collection<String>) rolesObj;
                                            authorities.addAll(
                                                    roles.stream()
                                                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                                                            .toList()
                                            );
                                        }
                                    }

                                    // Extract scopes
                                    Object scopeClaim = claims.get("scope");
                                    if (scopeClaim != null) {
                                        List<String> scopes = new ArrayList<>();

                                        if (scopeClaim instanceof String) {
                                            // Space-separated string
                                            scopes.addAll(Arrays.asList(((String) scopeClaim).split(" ")));
                                        } else if (scopeClaim instanceof Collection) {
                                            // Already a collection
                                            ((Collection<?>) scopeClaim).forEach(s -> scopes.add(s.toString()));
                                        }

                                        authorities.addAll(
                                                scopes.stream()
                                                        .filter(s -> !s.isEmpty())
                                                        .map(s -> new SimpleGrantedAuthority("SCOPE_" + s.toUpperCase()))
                                                        .toList()
                                        );
                                    }

                                    return new DefaultOAuth2AuthenticatedPrincipal(principalName, claims, authorities);
                                })
                        )
                )

                .build();
    }
}