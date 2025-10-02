package io.xenoss.exceptions;

/**
 * Exception thrown when HTTP client operations fail.
 */
public class HttpClientException extends FrameworkException {
    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
