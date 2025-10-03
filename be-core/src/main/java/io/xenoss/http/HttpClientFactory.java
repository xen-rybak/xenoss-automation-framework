package io.xenoss.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.xenoss.config.ConfigurationManager;
import io.xenoss.telemetry.HttpMetricsInterceptor;
import io.xenoss.telemetry.ConnectionEventListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * Factory class for creating and configuring OkHttpClient instances and mappers for HTTP requests.
 * Provides utility methods for building clients and request specifications.
 * Configuration values are loaded from testConfig.yaml and can be overridden via system properties.
 */
@Slf4j
public class HttpClientFactory {
    /** Maximum number of threads for the connection pool (configurable). */
    public static final int MAX_THREADS = ConfigurationManager.getConfig()
                                                              .getHttpConnectionPoolSize();
    /** Timeout for connections, reads, and writes (in seconds, configurable). */
    private static final int TIMEOUT_SECONDS = ConfigurationManager.getConfig()
                                                                   .getHttpTimeoutSeconds();
    /** Keep-alive duration for connections (in seconds, configurable). */
    private static final int KEEP_ALIVE_SECONDS = ConfigurationManager.getConfig()
                                                                      .getHttpKeepAliveSeconds();

    /** Shared connection pool for all OkHttpClient instances. */
    private static final ConnectionPool connectionPool = new ConnectionPool(
            MAX_THREADS, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS);

    /** Shared Jackson ObjectMapper for JSON serialization/deserialization. */
    @Getter
    private static final ObjectMapper objectMapper = createObjectMapper();
    /** Shared Jackson XmlMapper for XML serialization/deserialization. */
    @Getter
    private static final XmlMapper xmlMapper = createXmlMapper();

    /**
     * Creates a default OkHttpClient instance with telemetry and metrics interceptors.
     * @return a configured OkHttpClient
     */
    public static OkHttpClient createHttpClient() {
        return createHttpClient(false);
    }

    /**
     * Creates an OkHttpClient instance with optional HTTP logging.
     * Adds telemetry, metrics, and connection event listeners.
     * @param enableLogging whether to enable HTTP logging
     * @return a configured OkHttpClient
     */
    public static OkHttpClient createHttpClient(boolean enableLogging) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectionPool(connectionPool)
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        // Add event listener for connection tracking
        builder.eventListener(new ConnectionEventListener());

        // Add metrics interceptor for telemetry tracking
        builder.addInterceptor(new HttpMetricsInterceptor());

        if (enableLogging) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(log::debug);
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(loggingInterceptor);
        }

        return builder.build();
    }

    /**
     * Creates a default RequestSpecification using a new OkHttpClient and default headers.
     * @return a new RequestSpecification
     */
    public static RequestSpecification given() {
        return new RequestSpecification(createHttpClient(), objectMapper, new Headers(), "");
    }

    /**
     * Creates a RequestSpecification using the provided OkHttpClient and default headers.
     * @param client the OkHttpClient to use
     * @return a new RequestSpecification
     */
    public static RequestSpecification given(OkHttpClient client) {
        return new RequestSpecification(client, objectMapper, new Headers(), "");
    }

    /**
     * Creates and configures a Jackson ObjectMapper for JSON.
     * @return a configured ObjectMapper
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        return mapper;
    }

    /**
     * Creates and configures a Jackson XmlMapper for XML.
     * @return a configured XmlMapper
     */
    private static XmlMapper createXmlMapper() {
        XmlMapper mapper = new XmlMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        return mapper;
    }
}
