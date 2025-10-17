package com.hackathon.safenet.domain.exception;

/**
 * Exception for notification delivery failures.
 */
public class NotificationDeliveryException extends RuntimeException {
    public NotificationDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}