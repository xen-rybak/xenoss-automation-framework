package io.xenoss.exceptions;

/**
 * Base exception for all framework-related errors.
 * Provides a common hierarchy for framework exceptions.
 */
public class FrameworkException extends RuntimeException {
    public FrameworkException(String message) {
        super(message);
    }

    public FrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public FrameworkException(Throwable cause) {
        super(cause);
    }
}
