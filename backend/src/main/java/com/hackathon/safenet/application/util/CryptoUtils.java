package com.hackathon.safenet.application.util;

import com.hackathon.safenet.domain.exception.CryptoException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Cryptographic Utilities
 * Provides secure cryptographic operations for the application.
 * Centralizes crypto logic for better maintainability and security.
 * Features:
 * - HMAC signature computation and validation
 * - Constant-time string comparison to prevent timing attacks
 * - Secure random generation utilities
 * Security Considerations:
 * - Uses constant-time comparison for signature validation
 * - Properly handles cryptographic exceptions
 * - Follows secure coding practices
 */
@Slf4j
@UtilityClass
public class CryptoUtils {

    /**
     * Computes HMAC signature for the given payload
     * 
     * @param payload The data to sign
     * @param secret The secret key for HMAC
     * @param algorithm The HMAC algorithm (e.g., HmacSHA256)
     * @return Hex-encoded HMAC signature
     * @throws CryptoException if signature computation fails
     */
    public static String computeHmacSignature(String payload, String secret, String algorithm) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                algorithm
            );
            mac.init(secretKey);
            
            byte[] signatureBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(signatureBytes);
            
        } catch (NoSuchAlgorithmException e) {
            log.error("Unsupported HMAC algorithm: {}", algorithm, e);
            throw new CryptoException("Unsupported HMAC algorithm: " + algorithm, e);
        } catch (InvalidKeyException e) {
            log.error("Invalid secret key for HMAC computation", e);
            throw new CryptoException("Invalid secret key for HMAC computation", e);
        }
    }

    /**
     * Validates HMAC signature against expected signature
     * 
     * @param payload The original data
     * @param providedSignature The signature to validate
     * @param secret The secret key
     * @param algorithm The HMAC algorithm
     * @return true if signature is valid, false otherwise
     */
    public static boolean validateHmacSignature(String payload, String providedSignature, 
                                              String secret, String algorithm) {
        try {
            String computedSignature = computeHmacSignature(payload, secret, algorithm);
            return constantTimeEquals(providedSignature, computedSignature);
        } catch (CryptoException e) {
            log.error("Failed to validate HMAC signature", e);
            return false;
        }
    }

    /**
     * Constant-time string comparison to prevent timing attacks
     * This method compares two strings in constant time regardless of their content,
     * preventing timing-based side-channel attacks that could be used to guess
     * valid signatures character by character.
     * 
     * @param a First string to compare
     * @param b Second string to compare
     * @return true if strings are equal, false otherwise
     */
    public static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return Objects.equals(a, b);
        }
        
        if (a.length() != b.length()) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        
        return result == 0;
    }

    /**
     * Extracts signature from header value
     * Some systems prefix signatures with algorithm info (e.g., "sha256=abc123")
     * This method extracts the actual signature part.
     * 
     * @param headerValue The full header value
     * @return The signature part without prefix
     */
    public static String extractSignature(String headerValue) {
        if (headerValue == null || headerValue.trim().isEmpty()) {
            return null;
        }
        
        // Handle prefixed signatures like "sha256=abc123"
        if (headerValue.contains("=")) {
            String[] parts = headerValue.split("=", 2);
            if (parts.length == 2) {
                return parts[1].trim();
            }
        }
        
        return headerValue.trim();
    }
}