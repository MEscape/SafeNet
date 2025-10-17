package com.hackathon.safenet.domain.exception;

import lombok.Getter;

/**
 * Exception thrown when a cryptographic operation fails.
 *
 * <p>This exception is used for errors related to encryption, decryption,
 * hashing, HMAC validation, or other cryptographic operations.</p>
 *
 * <p>It includes an {@code errorCode} for i18n and message localization,
 * following the same convention as other domain exceptions.</p>
 *
 * @author SafeNet Development Team
 * @since 1.0.0
 */
@Getter
public class CryptoException extends RuntimeException {

    private final String errorCode;
    private final Object[] messageArgs;

    /**
     * Constructs a new crypto exception with a generic error code and message.
     *
     * @param message the error message
     */
    public CryptoException(String message) {
        super(message);
        this.errorCode = "error.crypto.operation_failed";
        this.messageArgs = new Object[0];
    }

    /**
     * Constructs a new crypto exception with a generic error code, message, and cause.
     *
     * @param message the error message
     * @param cause the cause of the exception
     */
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "error.crypto.operation_failed";
        this.messageArgs = new Object[0];
    }

    /**
     * Constructs a new crypto exception with a specific error code and arguments.
     *
     * @param errorCode the i18n error code
     * @param messageArgs arguments for localized message formatting
     */
    public CryptoException(String errorCode, Object... messageArgs) {
        super(errorCode);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }

    /**
     * Constructs a new crypto exception with a specific error code, cause, and arguments.
     *
     * @param errorCode the i18n error code
     * @param cause the cause of the exception
     * @param messageArgs arguments for localized message formatting
     */
    public CryptoException(String errorCode, Throwable cause, Object... messageArgs) {
        super(errorCode, cause);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }
}
