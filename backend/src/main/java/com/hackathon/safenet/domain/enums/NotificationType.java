package com.hackathon.safenet.domain.enums;

/**
 * Enumeration of notification types supported by the SafeNet application.
 * 
 * <p>This enum defines all possible notification types that can be sent
 * to users through various channels (WebSocket, push notifications, etc.).
 * Each type represents a specific event or action that requires user attention.</p>
 * 
 * <h3>Friend Request Notifications</h3>
 * <ul>
 *   <li><strong>FRIEND_REQUEST_RECEIVED:</strong> A new friend request was received</li>
 *   <li><strong>FRIEND_REQUEST_ACCEPTED:</strong> A sent friend request was accepted</li>
 *   <li><strong>FRIEND_REQUEST_REJECTED:</strong> A sent friend request was rejected</li>
 *   <li><strong>FRIEND_REQUEST_CANCELLED:</strong> A received friend request was cancelled</li>
 * </ul>
 * 
 * <h3>Location and Safety Notifications</h3>
 * <ul>
 *   <li><strong>LOCATION_UPDATE:</strong> A friend's location was updated</li>
 *   <li><strong>LOCATION_SHARING_ENABLED:</strong> A friend enabled location sharing</li>
 *   <li><strong>LOCATION_SHARING_DISABLED:</strong> A friend disabled location sharing</li>
 *   <li><strong>EMERGENCY_ALERT:</strong> Emergency situation detected</li>
 * </ul>
 * 
 * <h3>System and User Status Notifications</h3>
 * <ul>
 *   <li><strong>FRIENDSHIP_REMOVED:</strong> A friendship was removed</li>
 *   <li><strong>USER_ONLINE:</strong> A friend came online</li>
 *   <li><strong>USER_OFFLINE:</strong> A friend went offline</li>
 *   <li><strong>SYSTEM_MESSAGE:</strong> General system notification</li>
 * </ul>
 * 
 * @author SafeNet Development Team
 * @since 1.0.0
 */
public enum NotificationType {
    
    // Friend Request Notifications
    FRIEND_REQUEST_RECEIVED("notification.friend_request.received"),
    FRIEND_REQUEST_ACCEPTED("notification.friend_request.accepted"),
    FRIEND_REQUEST_REJECTED("notification.friend_request.rejected"),
    FRIEND_REQUEST_CANCELLED("notification.friend_request.cancelled"),
    
    // Location and Safety Notifications
    LOCATION_UPDATE("notification.location.update"),
    LOCATION_SHARING_ENABLED("notification.location.sharing_enabled"),
    LOCATION_SHARING_DISABLED("notification.location.sharing_disabled"),
    EMERGENCY_ALERT("notification.emergency.alert"),
    
    // Friendship Notifications
    FRIENDSHIP_REMOVED("notification.friendship.removed"),
    
    // User Status Notifications
    USER_ONLINE("notification.user.online"),
    USER_OFFLINE("notification.user.offline"),
    
    // System Notifications
    SYSTEM_MESSAGE("notification.system.message");
    
    private final String messageKey;
    
    /**
     * Constructor for NotificationType enum.
     * 
     * @param messageKey the internationalization key for the notification message
     */
    NotificationType(String messageKey) {
        this.messageKey = messageKey;
    }
    
    /**
     * Get the internationalization message key for this notification type.
     * 
     * @return the message key for i18n lookup
     */
    public String getMessageKey() {
        return messageKey;
    }
}