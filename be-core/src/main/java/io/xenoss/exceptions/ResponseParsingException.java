package io.xenoss.exceptions;

/**
 * Exception thrown when response parsing or deserialization fails.
 */
public class ResponseParsingException extends HttpClientException {
    public ResponseParsingException(String message) {
        super(message);
    }

    public ResponseParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
