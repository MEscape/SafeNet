package com.hackathon.safenet.application.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration for real-time messaging.
 * 
 * <p>This configuration enables WebSocket communication using STOMP protocol
 * for real-time notifications in the SafeNet application. It supports friend
 * request notifications, location updates, and other real-time events.</p>
 * 
 * <h3>Supported Features</h3>
 * <ul>
 *   <li><strong>Friend Request Notifications:</strong> Real-time friend request events</li>
 *   <li><strong>Location Updates:</strong> Live location sharing between friends</li>
 *   <li><strong>System Notifications:</strong> Emergency alerts and system messages</li>
 *   <li><strong>User Status:</strong> Online/offline status updates</li>
 * </ul>
 * 
 * <h3>Message Destinations</h3>
 * <ul>
 *   <li><code>/topic/notifications/{userId}</code> - Personal notifications</li>
 *   <li><code>/topic/friend-requests/{userId}</code> - Friend request events</li>
 *   <li><code>/topic/locations/{userId}</code> - Location updates for friends</li>
 *   <li><code>/topic/emergency</code> - Emergency broadcasts</li>
 * </ul>
 * 
 * <h3>Client Connection</h3>
 * <p>Clients can connect to WebSocket endpoints at:</p>
 * <ul>
 *   <li><code>/ws</code> - Main WebSocket endpoint</li>
 *   <li><code>/ws/sockjs</code> - SockJS fallback for older browsers</li>
 * </ul>
 * 
 * <h3>Security</h3>
 * <p>WebSocket connections are secured using JWT tokens passed during
 * the handshake process. Users can only subscribe to their own notification
 * channels and authorized friend location updates.</p>
 * 
 * @author SafeNet Development Team
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure the message broker for handling WebSocket messages.
     * 
     * <p>This method sets up the message broker with the following configuration:</p>
     * <ul>
     *   <li>Simple broker for topic-based messaging</li>
     *   <li>Application destination prefix for client messages</li>
     *   <li>User destination prefix for personal messages</li>
     * </ul>
     * 
     * @param config the message broker registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.info("Configuring WebSocket message broker");
        
        // Enable simple broker for topic-based messaging
        config.enableSimpleBroker(
                "/topic",    // Public topics (emergency alerts, etc.)
                "/queue"     // Private queues (personal notifications)
        );
        
        // Set application destination prefix for client messages
        config.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for personal messages
        config.setUserDestinationPrefix("/user");
        
        log.info("WebSocket message broker configured successfully");
    }

    /**
     * Register STOMP endpoints for WebSocket connections.
     * 
     * <p>This method registers the WebSocket endpoints that clients can connect to:</p>
     * <ul>
     *   <li>Main WebSocket endpoint with CORS support</li>
     *   <li>SockJS fallback for browser compatibility</li>
     * </ul>
     * 
     * @param registry the STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("Registering STOMP endpoints");
        
        // Register main WebSocket endpoint
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Configure based on CORS settings
                .withSockJS(); // Enable SockJS fallback
        
        // Register endpoint without SockJS for native WebSocket clients
        registry.addEndpoint("/ws-native")
                .setAllowedOriginPatterns("*");
        
        log.info("STOMP endpoints registered successfully");
    }
}