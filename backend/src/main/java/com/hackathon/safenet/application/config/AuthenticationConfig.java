package com.hackathon.safenet.application.config;

import com.hackathon.safenet.infrastructure.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Authentication configuration for the SafeNet application.
 *
 * <p>This configuration provides authentication-related beans including
 * password encoding and user details services. It handles both JWT-based
 * authentication for main endpoints and Basic Authentication for webhooks.</p>
 *
 * <h3>Authentication Methods</h3>
 * <ul>
 *   <li><strong>JWT Authentication:</strong> For main application endpoints using Keycloak tokens</li>
 *   <li><strong>Basic Authentication:</strong> For webhook endpoints using username/password</li>
 * </ul>
 *
 * <h3>Password Security</h3>
 * <ul>
 *   <li><strong>BCrypt Encoding:</strong> Industry-standard password hashing with salt</li>
 *   <li><strong>Configurable Strength:</strong> BCrypt work factor can be adjusted for security vs performance</li>
 *   <li><strong>Secure Defaults:</strong> Uses BCrypt strength of 12 for strong security</li>
 * </ul>
 *
 * <h3>Webhook Authentication</h3>
 * <p>Webhook endpoints use Basic Authentication with credentials configured via
 * application properties. This provides a simple but secure authentication
 * mechanism for external systems like Keycloak to send webhook notifications.</p>
 *
 */
@Configuration
@RequiredArgsConstructor
public class AuthenticationConfig {

    private final ApplicationProperties applicationProperties;

    /**
     * Configures the password encoder for the application.
     *
     * <p>Uses BCrypt password encoding with a strength of 12, which provides
     * strong security while maintaining reasonable performance. BCrypt automatically
     * handles salt generation and is resistant to rainbow table attacks.</p>
     *
     * @return the configured PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Configures the UserDetailsService for Basic Authentication.
     *
     * <p>This service is used for webhook endpoints that require Basic Authentication.
     * It creates an in-memory user store with credentials configured via application
     * properties. This is suitable for webhook authentication where only a single
     * service account is needed.</p>
     *
     * <h3>Configuration</h3>
     * <p>Webhook credentials are configured via:</p>
     * <ul>
     *   <li><code>safenet.keycloak.webhook.username</code> - The webhook username</li>
     *   <li><code>safenet.keycloak.webhook.password</code> - The webhook password (should be encrypted)</li>
     * </ul>
     *
     * @return the configured UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails webhookUser = User.builder()
                .username(applicationProperties.getKeycloak().getWebhook().getUsername())
                .password(passwordEncoder().encode(applicationProperties.getKeycloak().getWebhook().getPassword()))
                .roles("WEBHOOK")
                .build();

        return new InMemoryUserDetailsManager(webhookUser);
    }
}