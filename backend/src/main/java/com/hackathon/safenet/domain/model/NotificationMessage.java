package com.hackathon.safenet.domain.model;

import com.hackathon.safenet.domain.enums.NotificationType;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Simplified immutable notification message.
 * Contains only essential data for routing and delivery.
 */
@Value
@Builder
public class NotificationMessage {

    NotificationType type;
    String senderId;
    String receiverId;

    @Builder.Default
    LocalDateTime timestamp = LocalDateTime.now();

    @Builder.Default
    Priority priority = Priority.NORMAL;

    @Builder.Default
    Settings settings = Settings.DEFAULT;

    @Builder.Default
    Map<String, Object> data = Map.of();

    /**
     * Notification priority levels.
     */
    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }

    /**
     * Notification delivery settings.
     */
    @Value
    @Builder
    public static class Settings {

        public static final Settings DEFAULT = Settings.builder().build();

        public static final Settings URGENT = Settings.builder()
                .persistent(true)
                .pushNotification(true)
                .ttlSeconds(3600L)
                .build();

        @Builder.Default
        boolean persistent = true;

        @Builder.Default
        boolean pushNotification = false;

        @Builder.Default
        Long ttlSeconds = 86400L; // 24 hours
    }

    /**
     * Check if notification has expired based on TTL.
     */
    public boolean isExpired() {
        if (settings.getTtlSeconds() == null) {
            return false;
        }
        return timestamp.plusSeconds(settings.getTtlSeconds())
                .isBefore(LocalDateTime.now());
    }

    /**
     * Static factory for simple notifications with optional data.
     */
    public static NotificationMessage create(
            NotificationType type,
            String senderId,
            String receiverId,
            Map<String, Object> data) {
        return NotificationMessage.builder()
                .type(type)
                .senderId(senderId)
                .receiverId(receiverId)
                .data(data != null ? data : Map.of())
                .build();
    }

    /**
     * Static factory for urgent notifications with optional data.
     */
    public static NotificationMessage createUrgent(
            NotificationType type,
            String senderId,
            String receiverId,
            Map<String, Object> data) {
        return NotificationMessage.builder()
                .type(type)
                .senderId(senderId)
                .receiverId(receiverId)
                .priority(Priority.URGENT)
                .settings(Settings.URGENT)
                .data(data != null ? data : Map.of())
                .build();
    }
}
