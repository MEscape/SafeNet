package com.hackathon.safenet.domain.ports.outbound;

import com.hackathon.safenet.domain.model.NotificationMessage;

/**
 * Port for sending notifications.
 * Implementations handle the actual delivery mechanism.
 */
public interface NotificationPort {

    /**
     * Send a notification to a specific user.
     *
     * @param notification the notification to send
     */
    void send(NotificationMessage notification);

    /**
     * Send a notification to all users in a topic/group.
     *
     * @param topic the topic identifier
     * @param notification the notification to send
     */
    void sendToTopic(String topic, NotificationMessage notification);
}