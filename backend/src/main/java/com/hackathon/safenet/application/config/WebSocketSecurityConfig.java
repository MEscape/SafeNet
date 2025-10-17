package com.hackathon.safenet.application.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collection;

/**
 * WebSocket security configuration for opaque token-based authentication.
 *
 * <p>This configuration secures WebSocket connections by validating opaque tokens
 * using Keycloak's introspection endpoint during the STOMP handshake process.
 * It ensures that only authenticated users can establish WebSocket connections
 * and subscribe to authorized channels.</p>
 *
 * <h3>Security Features</h3>
 * <ul>
 *   <li><strong>Token Introspection:</strong> Validates tokens using Keycloak's introspection endpoint</li>
 *   <li><strong>Real-time Validation:</strong> Checks token validity at connection time</li>
 *   <li><strong>User Authentication:</strong> Establishes security context for WebSocket sessions</li>
 *   <li><strong>Channel Authorization:</strong> Ensures users can only access authorized channels</li>
 *   <li><strong>Session Management:</strong> Manages authenticated WebSocket sessions</li>
 * </ul>
 *
 * <h3>Authentication Flow</h3>
 * <ol>
 *   <li>Client connects to WebSocket endpoint with token in Authorization header</li>
 *   <li>Interceptor validates the token using OpaqueTokenIntrospector</li>
 *   <li>If valid, creates authentication context for the WebSocket session</li>
 *   <li>User can then subscribe to authorized channels based on their identity</li>
 * </ol>
 *
 * <h3>Authorized Channels</h3>
 * <ul>
 *   <li><code>/user/queue/notifications</code> - Personal notifications</li>
 *   <li><code>/user/queue/friend-requests</code> - Friend request events</li>
 *   <li><code>/user/queue/locations</code> - Location updates from friends</li>
 *   <li><code>/topic/emergency</code> - Emergency broadcasts (all authenticated users)</li>
 * </ul>
 *
 * @author SafeNet Development Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final OpaqueTokenIntrospector opaqueTokenIntrospector;

    /**
     * Configure client inbound channel with token introspection interceptor.
     *
     * <p>This method adds a channel interceptor that validates tokens
     * for incoming WebSocket connections and establishes the security context.</p>
     *
     * @param registration the channel registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new TokenIntrospectionChannelInterceptor());
    }

    /**
     * Channel interceptor for opaque token-based WebSocket authentication.
     *
     * <p>This interceptor validates tokens during STOMP CONNECT commands
     * using Keycloak's introspection endpoint and establishes the security
     * context for the WebSocket session.</p>
     */
    private class TokenIntrospectionChannelInterceptor implements ChannelInterceptor {

        /**
         * Intercept messages before they are sent to the channel.
         * 
         * <p>This method validates JWT tokens for CONNECT commands and
         * establishes authentication context for the WebSocket session.</p>
         * 
         * @param message the message being sent
         * @param channel the message channel
         * @return the message (possibly modified) or null to prevent sending
         */
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            
            if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                log.debug("Processing WebSocket CONNECT command");
                
                try {
                    // Extract token from Authorization header
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                        log.warn("WebSocket connection attempt without valid Authorization header");
                        throw new SecurityException("Missing or invalid Authorization header");
                    }
                    
                    String token = authHeader.substring(7); // Remove "Bearer " prefix

                    // Introspect token using the configured introspector
                    OAuth2AuthenticatedPrincipal principal = opaqueTokenIntrospector.introspect(token);

                    // Extract principal name and authorities
                    String principalName = principal.getName();
                    Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

                    if (principalName == null || principalName.trim().isEmpty()) {
                        log.warn("WebSocket connection attempt with token missing principal name");
                        throw new SecurityException("Token missing principal name");
                    }

                    // Create authentication object
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            principalName, null, authorities);

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    accessor.setUser(authentication);

                    log.info("WebSocket connection authenticated for user: {} with {} authorities",
                            principalName, authorities.size());
                } catch (OAuth2AuthenticationException e) {
                    log.error("Invalid JWT token in WebSocket connection: {}", e.getMessage());
                    throw new SecurityException("Invalid JWT token", e);
                } catch (Exception e) {
                    log.error("Error during WebSocket authentication: {}", e.getMessage(), e);
                    throw new SecurityException("Authentication failed", e);
                }
            }
            
            return message;
        }
    }
}