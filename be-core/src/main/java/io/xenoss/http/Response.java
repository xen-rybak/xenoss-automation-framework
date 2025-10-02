package io.xenoss.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.xenoss.exceptions.HttpClientException;
import io.xenoss.exceptions.ResponseParsingException;
import io.xenoss.utils.SerializationUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Wrapper for OkHttp response providing convenient access to body, headers, status, and deserialization.
 * Supports JSON and other formats via Jackson ObjectMapper.
 * IMPORTANT: This class takes ownership of the OkHttp Response and closes it after reading the body.
 */
@Slf4j
public class Response {
    /**
     * The underlying OkHttp response object.
     */
    private final okhttp3.Response okHttpResponse;
    /**
     * The response body as a string.
     */
    private final String responseBody;
    /**
     * The ObjectMapper for JSON deserialization.
     */
    private final ObjectMapper objectMapper;
    /**
     * The response headers.
     */
    @Getter
    private final Headers headers;

    /**
     * Constructs a Response wrapper from an OkHttp response and ObjectMapper.
     * Reads and stores the response body and headers.
     * IMPORTANT: This constructor closes the OkHttp response after reading the body to prevent resource leaks.
     *
     * @param response     the OkHttp response (will be closed by this constructor)
     * @param objectMapper the ObjectMapper for deserialization
     */
    public Response(okhttp3.Response response, ObjectMapper objectMapper) {
        this.okHttpResponse = response;
        this.objectMapper = objectMapper;
        this.headers = convertHeaders(response.headers());

        try (response) {
            // Read body and immediately close the response to prevent connection leaks
            this.responseBody = response.body() != null ? response.body()
                                                                  .string() : "";
        } catch (IOException e) {
            throw new HttpClientException("Failed to read response body", e);
        }
    }

    /**
     * Converts OkHttp headers to custom Headers object.
     *
     * @param okHttpHeaders the OkHttp headers
     * @return a Headers object
     */
    private Headers convertHeaders(okhttp3.Headers okHttpHeaders) {
        return new Headers(okHttpHeaders.toMultimap()
                                        .entrySet()
                                        .stream()
                                        .flatMap(entry -> entry.getValue()
                                                               .stream()
                                                               .map(value -> new Header(entry.getKey(), value)))
                                        .toList());
    }

    /**
     * Deserializes the response body to the given class type using Jackson.
     *
     * @param clazz the target class
     * @param <T>   the type to deserialize to
     * @return the deserialized object, or null if body is empty
     */
    public <T> T as(Class<T> clazz) {
        try {
            if (responseBody == null || responseBody.trim()
                                                    .isEmpty()) {
                return null;
            }
            return SerializationUtils.fromJson(responseBody, clazz);
        } catch (Exception e) {
            throw new ResponseParsingException("Failed to deserialize response to " + clazz.getSimpleName() +
                    ". Response body: " + responseBody, e);
        }
    }

    /**
     * Deserializes the response body to the given type reference using Jackson.
     *
     * @param typeRef the target type reference
     * @param <T>     the type to deserialize to
     * @return the deserialized object, or null if body is empty
     */
    public <T> T as(TypeReference<T> typeRef) {
        try {
            if (responseBody == null || responseBody.trim()
                                                    .isEmpty()) {
                return null;
            }
            return objectMapper.readValue(responseBody, typeRef);
        } catch (IOException e) {
            throw new ResponseParsingException("Failed to deserialize response to " + typeRef.getType() +
                    ". Response body: " + responseBody, e);
        }
    }

    /**
     * Returns the response body as a string.
     *
     * @return the response body string
     */
    public String asString() {
        return responseBody != null ? responseBody : "";
    }

    /**
     * Returns the response body as a pretty-printed JSON string if possible.
     * If not valid JSON, returns the raw body.
     *
     * @return the pretty-printed response body
     */
    public String asPrettyString() {
        try {
            if (responseBody == null || responseBody.trim()
                                                    .isEmpty()) {
                return "";
            }
            Object json = objectMapper.readValue(responseBody, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter()
                               .writeValueAsString(json);
        } catch (IOException e) {
            return responseBody; // Return as-is if not valid JSON
        }
    }

    /**
     * Returns the response body as a byte array.
     *
     * @return the response body bytes
     */
    public byte[] asByteArray() {
        return responseBody != null ? responseBody.getBytes() : new byte[0];
    }

    /**
     * Returns the HTTP status code of the response.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return okHttpResponse.code();
    }

    /**
     * Returns the HTTP status code of the response (alias).
     *
     * @return the status code
     */
    public int statusCode() {
        return getStatusCode();
    }

    /**
     * Returns the Content-Type header value, or text/plain if not present.
     *
     * @return the content type string
     */
    public String contentType() {
        String contentType = okHttpResponse.header("Content-Type");
        return contentType != null ? contentType : "text/plain";
    }

    /**
     * Returns the value of the specified header, or null if not present.
     *
     * @param name the header name
     * @return the header value, or null
     */
    public String getHeader(String name) {
        return okHttpResponse.header(name);
    }

    /**
     * Returns all headers as a map of list of strings.
     *
     * @return the headers map
     */
    public Map<String, List<String>> headers() {
        return okHttpResponse.headers()
                             .toMultimap();
    }

    /**
     * Checks if the response body is empty (null or blank).
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return responseBody == null || responseBody.trim()
                                                   .isEmpty();
    }

    /**
     * Returns a ResponseExtractor for extracting response details.
     *
     * @return the ResponseExtractor
     */
    public ResponseExtractor then() {
        return new ResponseExtractor(this);
    }

    /**
     * Returns a ResponseAssertion for asserting response properties.
     *
     * @return the ResponseAssertion
     */
    public ResponseAssertion assertThat() {
        return new ResponseAssertion(this);
    }

    public record ResponseExtractor(Response response) {

        public Response extract() {
            return response;
        }

        public ResponseAssertion assertThat() {
            return new ResponseAssertion(response);
        }

        public byte[] asByteArray() {
            return response.asByteArray();
        }
    }

    public record ResponseAssertion(Response response) {

        public ResponseAssertion statusCode(int expectedStatusCode) {
            int actualStatusCode = response.getStatusCode();
            if (actualStatusCode != expectedStatusCode) {
                throw new AssertionError(String.format("Expected status code %d but was %d",
                        expectedStatusCode, actualStatusCode));
            }
            return this;
        }

        public ResponseExtractor extract() {
            return new ResponseExtractor(response);
        }
    }
}
