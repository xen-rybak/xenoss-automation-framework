package io.xenoss.backend.client;

import io.xenoss.config.ConfigurationManager;
import io.xenoss.http.ContentType;
import io.xenoss.http.Header;
import io.xenoss.http.Headers;
import io.xenoss.http.HttpClientFactory;
import io.xenoss.http.RequestSpecification;
import io.xenoss.http.Response;
import io.xenoss.telemetry.TelemetryData;
import io.xenoss.telemetry.ConnectionPoolMetrics;
import io.xenoss.utils.FileUtils;
import io.xenoss.utils.RandomUtils;
import io.xenoss.utils.SerializationUtils;
import io.xenoss.utils.ThreadingUtils;
import io.xenoss.utils.WaitUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.testng.TestException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static io.xenoss.http.HttpClientFactory.MAX_THREADS;

@Slf4j
public abstract class BaseClient {
    // Config
    private static final boolean SILENT = ConfigurationManager.getConfig()
                                                              .getIsSilent();
    private static final String RESULTS_DIR_PATH = String.format("build/reports/rawTestsOutput/%s", RandomUtils.currentTimestamp());

    // Logging
    private static final ThreadLocal<String> prevRequest = new ThreadLocal<>();
    private static final ThreadLocal<String> prevResponseMessage = new ThreadLocal<>();
    private static final ThreadLocal<Integer> prevResponseStatus = new ThreadLocal<>();

    private final ThreadLocal<OkHttpClient> httpClient = new ThreadLocal<>();
    private final Supplier<RequestSpecification> requestSpecification;
    private final String baseUrl;

    private final boolean gzip;
    private final Headers headers;

    static {
        FileUtils.makeDir(RESULTS_DIR_PATH);
    }

    static {
        ThreadingUtils.startAsync(() -> {
            WaitUtils.forSeconds(1);

            // System telemetry with detailed connection metrics
            Map<String, String> systemMetrics = new java.util.LinkedHashMap<>();
            systemMetrics.put("🌐 HTTP Client", "OkHttp v4.12.0");
            systemMetrics.put("🏊 Max Connection Pool Size", String.valueOf(MAX_THREADS));
            systemMetrics.put("🔧 GZIP Enabled", "Auto-negotiated");

            // Add connection pool metrics
            systemMetrics.putAll(ConnectionPoolMetrics.getMetrics());

            // Runtime information
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;

            systemMetrics.put("💾 Memory Used", String.format("%.1f MB", usedMemory / 1024.0 / 1024.0));
            systemMetrics.put("💽 Memory Total", String.format("%.1f MB", totalMemory / 1024.0 / 1024.0));
            systemMetrics.put("⚡ Available Processors", String.valueOf(runtime.availableProcessors()));

            TelemetryData.updateSystemMetrics(systemMetrics);

            return null;
        }, "BaseClient-Telemetry", true);
    }


    /**
     * Constructs a BaseClient with the specified base URL.
     * Uses default settings (no gzip).
     *
     * @param baseUrl the base URL for requests
     */
    public BaseClient(String baseUrl) {
        this(baseUrl, false);
    }

    /**
     * Constructs a BaseClient with the specified base URL and gzip setting.
     * Uses default headers.
     *
     * @param baseUrl the base URL for requests
     * @param gzip whether to enable gzip compression
     */
    public BaseClient(String baseUrl, boolean gzip) {
        this(baseUrl, new Headers(), gzip);
    }

    /**
     * Constructs a BaseClient with the specified base URL, headers, and gzip setting.
     * Adds keep-alive and gzip headers as needed.
     *
     * @param baseUrl the base URL for requests
     * @param headers custom headers to use
     * @param gzip whether to enable gzip compression
     */
    public BaseClient(String baseUrl, Headers headers, boolean gzip) {
        this.baseUrl = baseUrl;
        this.gzip = gzip;
        var headersList = new ArrayList<>(headers.asList());
        headersList.add(new Header("Connection", "keep-alive"));

        if (gzip) {
            headersList.add(new Header("Content-Encoding", "gzip"));
            headersList.add(new Header("Accept-Encoding", "gzip"));
        }
        this.headers = new Headers(headersList);

        this.requestSpecification = () -> HttpClientFactory.given(getHttpClient())
                                                           .headers(this.headers)
                                                           .urlEncodingEnabled(false)
                                                           .contentType(ContentType.JSON);
    }

    /**
     * Constructs a BaseClient with basic authentication.
     *
     * @param baseUrl the base URL for requests
     * @param userName the username for basic auth
     * @param password the password for basic auth
     */
    public BaseClient(String baseUrl, String userName, String password) {
        this.baseUrl = baseUrl;
        this.gzip = false;
        this.headers = null;

        // For basic auth, we'll add the Authorization header
        String credentials = java.util.Base64.getEncoder()
                                             .encodeToString((userName + ":" + password).getBytes());
        Headers authHeaders = new Headers(new Header("Authorization", "Basic " + credentials));

        this.requestSpecification = () -> HttpClientFactory.given(getHttpClient())
                                                           .headers(authHeaders)
                                                           .contentType(ContentType.JSON);
    }

    /**
     * Returns the current request specification, thread-safe.
     *
     * @return the current RequestSpecification
     */
    private synchronized RequestSpecification getRequestSpecification() {
        return requestSpecification.get();
    }

    /**
     * Returns the OkHttpClient for the current thread, creating it if necessary.
     *
     * @return the OkHttpClient instance
     */
    private OkHttpClient getHttpClient() {
        if (httpClient.get() == null) {
            httpClient.set(HttpClientFactory.createHttpClient());
        }
        return httpClient.get();
    }

    /**
     * Sends a POST request with a file as the body.
     * Logs the request and returns the response.
     *
     * @param path the endpoint path
     * @param file the file to send
     * @return the response from the server
     */
    protected Response postFile(String path, File file) {
        var url = String.format("%s/%s", baseUrl, path);

        if (!SILENT) {
            log.info("Sending POST to {} with file:\n{}", url, file.getPath());
        }

        return extractResponse(() -> getRequestSpecification().contentType(ContentType.BINARY)
                                                              .body(file)
                                                              .post(url));
    }

    /**
     * Sends a POST request with a body and no query parameters.
     *
     * @param path the endpoint path
     * @param body the request body
     * @param <T> the type of the body
     * @return the response from the server
     */
    protected <T> Response post(String path, T body) {
        return post(path, body, Map.of());
    }

    /**
     * Sends a POST request with a body and query parameters.
     * Handles gzip compression if enabled.
     *
     * @param path the endpoint path
     * @param body the request body
     * @param queryParams the query parameters
     * @param <T> the type of the body
     * @return the response from the server
     */
    protected <T> Response post(String path, T body, Map<String, String> queryParams) {
        var url = buildUrl(path);
        String bodyString = body == null
                ? "null"
                : (body instanceof String) ? (String) body : SerializationUtils.toJson(body);

        if (!SILENT) {
            var requestMessage = String.format("Sending POST to %s with headers:\n\t%s\nand %s body:\n%s",
                    queryParams == null || queryParams.isEmpty()
                            ? url
                            : String.format("%s?%s", url, queryParams.entrySet()
                                                                     .stream()
                                                                     .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                                                                     .collect(Collectors.joining("&"))),
                    String.join("\n\t", headers.asList()
                                               .stream()
                                               .map(header -> String.format("%s: %s", header.getName(), header.getValue()))
                                               .toList()),
                    gzip ? "gzipped " : "",
                    bodyString);
            log.info(requestMessage.equalsIgnoreCase(prevRequest.get()) ? "Repeating previous request..." : requestMessage);
            prevRequest.set(requestMessage);
        }

        var requestSpecWithBody = body == null
                ? getRequestSpecification()
                : gzip
                ? getRequestSpecification().body(gzipCompress(bodyString))
                : getRequestSpecification().body(bodyString);

        return extractResponse(() -> requestSpecWithBody.queryParams(queryParams)
                                                        .post(url));
    }

    /**
     * Sends a GET request to the specified path with no parameters.
     *
     * @param path the endpoint path
     * @return the response from the server
     */
    protected Response get(String path) {
        return get(path, null);
    }

    /**
     * Sends a GET request to the specified path with query parameters.
     *
     * @param path the endpoint path
     * @param params the query parameters as a map
     * @return the response from the server
     */
    protected Response get(String path, Map<String, Object[]> params) {
        return get(path, params, true);
    }

    /**
     * Sends a GET request to the specified path with query parameters and option to send nulls.
     *
     * @param path the endpoint path
     * @param params the query parameters as a map
     * @param sendNullParams whether to include parameters with null values
     * @return the response from the server
     */
    protected Response get(String path, Map<String, Object[]> params, boolean sendNullParams) {
        return get(path, params, sendNullParams, true);
    }

    /**
     * Sends a GET request to the specified path with query parameters, option to send nulls, and redirect handling.
     * Filters out null values if sendNullParams is false.
     * Logs request details if SILENT is false.
     *
     * @param path the endpoint path
     * @param params the query parameters as a map
     * @param sendNullParams whether to include parameters with null values
     * @param useRedirect whether to follow redirects
     * @return the response from the server
     */
    protected Response get(String path, Map<String, Object[]> params, boolean sendNullParams, boolean useRedirect) {
        String url = buildUrl(path);

        var requestSpecWithParams = getRequestSpecification().redirects()
                                                             .follow(useRedirect);

        if (params == null || params.isEmpty()) {
            if (!SILENT) {
                log.info("Sending GET to {}", url);
            }
        } else {
            // Filter out null values from params if sendNullParams is false
            var filteredParams = sendNullParams
                    ? params
                    : params.entrySet()
                            .stream()
                            .filter(entry -> entry.getValue() != null)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (!SILENT) {
                // Log each parameter and its value(s)
                log.info("Sending GET to {} with params:\n{}", url,
                        filteredParams.entrySet()
                                      .stream()
                                      .map(entry -> String.format("%s=%s", entry.getKey(), Arrays.stream(entry.getValue())
                                                                                                 .map(Object::toString)
                                                                                                 .collect(Collectors.joining(", "))))
                                      .collect(Collectors.joining("\n")));
            }
            // Add each parameter to the request specification
            for (var param : filteredParams.entrySet()) {
                requestSpecWithParams = requestSpecWithParams.param(param.getKey(), param.getValue());
            }
        }

        RequestSpecification finalRequestSpecWithParams = requestSpecWithParams;
        // Execute the GET request and extract the response
        return extractResponse(() -> finalRequestSpecWithParams.get(url));
    }

    /**
     * Sends a DELETE request to the specified path.
     * Logs the request and returns the response.
     *
     * @param path the endpoint path
     * @return the response from the server
     */
    protected Response delete(String path) {
        String url = buildUrl(path);

        if (!SILENT) {
            log.info("Sending DELETE to {}", url);
        }
        return extractResponse(() -> getRequestSpecification().delete(url));
    }

    /**
     * Builds a full URL from the base URL and path.
     *
     * @param path the endpoint path
     * @return the full URL
     */
    private String buildUrl(String path) {
        return StringUtils.isEmpty(baseUrl)
                ? path
                : String.format("%s/%s", baseUrl, path);
    }

    /**
     * Executes the request and logs the response.
     * Handles long responses by saving to file.
     *
     * @param requestExecutor the request to execute
     * @return the extracted response
     */
    private Response extractResponse(Callable<Response> requestExecutor) {
        var extractedResponse = sendRequest(requestExecutor, 2);

        String logMessage;
        var prettifiedResponse = extractedResponse.asPrettyString();
        var contentType = extractedResponse.contentType()
                                           .split(";")[0];

        if (prettifiedResponse.trim()
                              .isEmpty()) {
            logMessage = "Response body is empty";
        } else if (Arrays.asList(ContentType.JSON.getContentTypeStrings())
                         .contains(contentType)
                || Arrays.asList(ContentType.XML.getContentTypeStrings())
                         .contains(contentType)
                || contentType.startsWith("text")) {
            logMessage = String.format("Response body of type \"%s\":\n%s", contentType, prettifiedResponse);
        } else {
            logMessage = String.format("Response body of type %s cannot be printed", contentType);
        }

        if (!SILENT) {
            var statusCode = extractedResponse.getStatusCode();
            if (logMessage.equalsIgnoreCase(prevResponseMessage.get()) && statusCode == prevResponseStatus.get()) {
                log.info("Same result");
            } else {
                log.info("Response status: {}", statusCode);
                if (logMessage.length() > 3000) {
                    var fileExtension = getFileExtension(contentType);

                    var path = FileUtils.printToFile(
                            String.format("%s/%s.%s", RESULTS_DIR_PATH, RandomUtils.currentTimestampWithMilliseconds(), fileExtension),
                            logMessage);
                    log.info("Response is too long. Storing to file:\n{}", path);
                } else {
                    log.info(logMessage);
                }
            }
            prevResponseMessage.set(logMessage);
            prevResponseStatus.set(statusCode);
        }
        return extractedResponse;
    }

    /**
     * Executes the request, retrying up to the specified number of attempts on failure.
     * Logs errors and retries if needed.
     *
     * @param requestExecutor the request to execute
     * @param attempts number of retry attempts
     * @return the response from the server
     */
    private Response sendRequest(Callable<Response> requestExecutor, int attempts) {
        try {
            return requestExecutor.call();
        } catch (Throwable t) {
            log.error(String.format("Error sending request!\nHeaders: %s", String.join("",
                    headers.asList()
                           .stream()
                           .map(header -> String.format("%s:%s", header.getName(), header.getValue()))
                           .toList())), t);

            if (attempts > 0) {
                WaitUtils.forSeconds(1);
                return sendRequest(requestExecutor, attempts - 1);
            } else {
                throw new RuntimeException(t);
            }
        }
    }

    /**
     * Returns the file extension for the given content type.
     *
     * @param contentType the content type string
     * @return the file extension (json, xml, html, or txt)
     */
    private static String getFileExtension(String contentType) {
        var fileExtension = "txt";
        if (Arrays.asList(ContentType.JSON.getContentTypeStrings())
                  .contains(contentType)) {
            fileExtension = "json";
        } else if (Arrays.asList(ContentType.XML.getContentTypeStrings())
                         .contains(contentType)) {
            fileExtension = "xml";
        } else if (Arrays.asList(ContentType.HTML.getContentTypeStrings())
                         .contains(contentType)) {
            fileExtension = "html";
        }
        return fileExtension;
    }

    /**
     * Compresses the given string using GZIP.
     *
     * @param data the string to compress
     * @return the compressed byte array
     */
    private static byte[] gzipCompress(String data) {
        if (data == null || data.isEmpty()) {
            return new byte[0];
        }

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos)) {

            gzipOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
            gzipOutputStream.finish();

            return bos.toByteArray();
        } catch (IOException e) {
            throw new TestException(e);
        }
    }

    /**
     * Decompresses the given GZIP-compressed byte array to a string.
     *
     * @param gzippedData the compressed byte array
     * @return the decompressed string
     */
    public static String gzipDecompress(byte[] gzippedData) {
        if (gzippedData == null || gzippedData.length == 0) {
            return "";
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPInputStream gis = new GZIPInputStream(new java.io.ByteArrayInputStream(gzippedData))) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new TestException(e);
        }
    }
}
