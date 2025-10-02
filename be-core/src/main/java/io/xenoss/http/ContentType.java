package io.xenoss.http;

/**
 * Enum representing supported HTTP content types for requests and responses.
 * Provides utility methods for retrieving content type strings.
 */
public enum ContentType {
    /** JSON content type (application/json) */
    JSON("application/json"),
    /** XML content type (application/xml) */
    XML("application/xml"),
    /** HTML content type (text/html) */
    HTML("text/html"),
    /** Plain text content type (text/plain) */
    TEXT("text/plain"),
    /** URL-encoded form content type (application/x-www-form-urlencoded) */
    URLENC("application/x-www-form-urlencoded"),
    /** Binary content type (application/octet-stream) */
    BINARY("application/octet-stream"),
    /** Wildcard content type (any) */
    ANY("*/*");

    /** The string representation of the content type. */
    private final String contentType;

    /**
     * Constructs a ContentType enum value.
     * @param contentType the string representation of the content type
     */
    ContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Returns the string representation of this content type.
     * @return the content type string
     */
    public String getContentTypeString() {
        return contentType;
    }

    /**
     * Returns an array containing the string representation of this content type.
     * @return an array with the content type string
     */
    public String[] getContentTypeStrings() {
        return new String[]{contentType};
    }

    /**
     * Returns the string representation of this content type.
     * @return the content type string
     */
    @Override
    public String toString() {
        return contentType;
    }
}
