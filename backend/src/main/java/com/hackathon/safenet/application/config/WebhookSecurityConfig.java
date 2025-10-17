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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Security configuration specifically for webhook endpoints.
 *
 * <p>This configuration handles webhook endpoints with Basic Authentication only.
 * It has higher precedence (order 1) to ensure webhook requests are processed
 * before the main filter chain.</p>
 *
 * <h3>Security Features</h3>
 * <ul>
 *   <li><strong>Basic Authentication:</strong> Username/password authentication for webhooks</li>
 *   <li><strong>Stateless Sessions:</strong> No server-side session storage</li>
 *   <li><strong>Security Headers:</strong> HSTS, Content-Type, Frame Options, Referrer Policy</li>
 *   <li><strong>HTTPS Enforcement:</strong> Configurable redirect to secure connections</li>
 * </ul>
 */
@Configuration
@RequiredArgsConstructor
public class WebhookSecurityConfig {

    private final ApplicationProperties applicationProperties;
    private final CorsSecurityConfig corsSecurityConfig;

    /**
     * Configures the security filter chain for webhook endpoints.
     *
     * <p>This filter chain handles webhook endpoints with Basic Authentication only.
     * It has higher precedence (order 1) to ensure webhook requests are processed
     * before the main filter chain.
     *
     * @param http the HttpSecurity configuration object
     * @return the configured SecurityFilterChain for webhook endpoints
     * @throws Exception if security configuration fails
     */
    @Bean
    @Order(1)
    public SecurityFilterChain webhookFilterChain(HttpSecurity http) throws Exception {
        return http
                // Apply only to webhook endpoints
                .securityMatcher("/api/v1/hooks/keycloak/**")

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

                // Configure authorization - all webhook endpoints require authentication
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )

                // Configure Basic Authentication only
                .httpBasic(httpBasic -> httpBasic
                        .realmName("Webhook Authentication")
                )

                .build();
    }
}