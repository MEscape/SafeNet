package com.hackathon.safenet.domain.model;

import java.time.LocalDateTime;

/**
 * WebSocket payload for notification delivery.
 * Separated from domain model for presentation concerns.
 */
public record NotificationPayload(
        String type,
        String senderId,
        String receiverId,
        LocalDateTime timestamp,
        String priority,
        boolean persistent,
        boolean pushNotification,
        long ttlSeconds
) {

    /**
     * Create payload from domain notification.
     */
    public static NotificationPayload from(NotificationMessage notification) {
        return new NotificationPayload(
                notification.getType().name(),
                notification.getSenderId(),
                notification.getReceiverId(),
                notification.getTimestamp(),
                notification.getPriority().name(),
                notification.getSettings().isPersistent(),
                notification.getSettings().isPushNotification(),
                notification.getSettings().getTtlSeconds()
        );
    }
}