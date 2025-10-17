package com.hackathon.safenet.infrastructure.adapters.notification;

import com.hackathon.safenet.domain.exception.NotificationDeliveryException;
import com.hackathon.safenet.domain.model.NotificationMessage;
import com.hackathon.safenet.domain.model.NotificationPayload;
import com.hackathon.safenet.domain.ports.outbound.NotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket implementation of NotificationPort.
 * Handles delivery via Spring WebSocket/STOMP.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationAdapter implements NotificationPort {

    private static final String USER_QUEUE = "/queue/notifications";
    private static final String TOPIC_PREFIX = "/topic/";

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void send(NotificationMessage notification) {
        validate(notification);

        if (notification.isExpired()) {
            log.debug("Skipping expired notification: {}", notification.getType());
            return;
        }

        try {
            NotificationPayload payload = NotificationPayload.from(notification);

            messagingTemplate.convertAndSendToUser(
                    notification.getReceiverId(),
                    USER_QUEUE,
                    payload
            );

            log.debug("Sent {} notification to user {}",
                    notification.getType(), notification.getReceiverId());

        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}",
                    notification.getReceiverId(), e.getMessage());
            throw new NotificationDeliveryException(
                    "Failed to deliver notification", e);
        }
    }

    @Override
    public void sendToTopic(String topic, NotificationMessage notification) {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Topic cannot be null or blank");
        }

        validate(notification);

        try {
            NotificationPayload payload = NotificationPayload.from(notification);

            messagingTemplate.convertAndSend(
                    TOPIC_PREFIX + topic,
                    payload
            );

            log.debug("Sent {} notification to topic {}",
                    notification.getType(), topic);

        } catch (Exception e) {
            log.error("Failed to send notification to topic {}: {}",
                    topic, e.getMessage());
            throw new NotificationDeliveryException(
                    "Failed to deliver notification to topic", e);
        }
    }

    private void validate(NotificationMessage notification) {
        if (notification == null) {
            throw new IllegalArgumentException("Notification cannot be null");
        }
        if (notification.getType() == null) {
            throw new IllegalArgumentException("Notification type is required");
        }
        if (notification.getReceiverId() == null || notification.getReceiverId().isBlank()) {
            throw new IllegalArgumentException("Receiver ID is required");
        }
    }
}