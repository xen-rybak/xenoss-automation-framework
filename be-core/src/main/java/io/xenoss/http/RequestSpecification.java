package io.xenoss.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.xenoss.exceptions.HttpClientException;
import io.xenoss.utils.SerializationUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Builder and executor for HTTP requests using OkHttpClient.
 * Supports configuration of headers, content type, query parameters, body, and redirect behavior.
 */
@Slf4j
public class RequestSpecification {
    /** Query parameters for the request. */
    protected final Map<String, String> queryParams = new HashMap<>();
    /** Content type for the request body. */
    protected ContentType contentType = ContentType.JSON;
    /** Whether URL encoding is enabled for query parameters. */
    protected boolean urlEncodingEnabled = true;
    /** Whether to follow redirects for the request. */
    protected boolean followRedirects = true;

    /** The OkHttpClient used to execute requests. */
    private final OkHttpClient httpClient;
    /** The ObjectMapper for JSON serialization/deserialization. */
    private final ObjectMapper objectMapper;
    /** Default headers for the request. */
    private final Headers defaultHeaders;
    /** The base URL for requests. */
    private final String baseUrl;

    /**
     * Constructs a RequestSpecification with the given client, mapper, headers, and base URL.
     * @param httpClient the OkHttpClient to use
     * @param objectMapper the ObjectMapper for JSON
     * @param defaultHeaders default headers to include
     * @param baseUrl the base URL for requests
     */
    public RequestSpecification(OkHttpClient httpClient, ObjectMapper objectMapper,
                              Headers defaultHeaders, String baseUrl) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.defaultHeaders = defaultHeaders != null ? defaultHeaders : new Headers();
        this.baseUrl = baseUrl;
    }

    /**
     * Returns a new RequestSpecification with the given headers.
     * Copies all other settings from the current specification.
     * @param headers the headers to use
     * @return a new RequestSpecification
     */
    public RequestSpecification headers(Headers headers) {
        RequestSpecification spec = new RequestSpecification(httpClient, objectMapper, headers, baseUrl);
        spec.contentType = this.contentType;
        spec.urlEncodingEnabled = this.urlEncodingEnabled;
        spec.followRedirects = this.followRedirects;
        spec.queryParams.putAll(this.queryParams);
        return spec;
    }

    /**
     * Sets the content type for the request body.
     * @param contentType the content type to use
     * @return this RequestSpecification
     */
    public RequestSpecification contentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Enables or disables URL encoding for query parameters.
     * @param enabled true to enable, false to disable
     * @return this RequestSpecification
     */
    public RequestSpecification urlEncodingEnabled(boolean enabled) {
        this.urlEncodingEnabled = enabled;
        return this;
    }

    /**
     * Returns a RedirectSpecification for configuring redirect behavior.
     * @return a RedirectSpecification
     */
    public RedirectSpecification redirects() {
        return new RedirectSpecification(this);
    }

    /**
     * Returns a new RequestSpecification with the given query parameters added.
     * Copies all other settings from the current specification.
     * @param params the query parameters to add
     * @return a new RequestSpecification
     */
    public RequestSpecification queryParams(Map<String, String> params) {
        RequestSpecification spec = new RequestSpecification(httpClient, objectMapper, defaultHeaders, baseUrl);
        spec.contentType = this.contentType;
        spec.urlEncodingEnabled = this.urlEncodingEnabled;
        spec.followRedirects = this.followRedirects;
        spec.queryParams.putAll(this.queryParams);
        if (params != null) {
            spec.queryParams.putAll(params);
        }
        return spec;
    }

    /**
     * Returns a new RequestSpecification with the given parameter and values added.
     * Multiple values are joined with commas.
     * Copies all other settings from the current specification.
     * @param key the parameter name
     * @param values the parameter values
     * @return a new RequestSpecification
     */
    public RequestSpecification param(String key, Object... values) {
        RequestSpecification spec = new RequestSpecification(httpClient, objectMapper, defaultHeaders, baseUrl);
        spec.contentType = this.contentType;
        spec.urlEncodingEnabled = this.urlEncodingEnabled;
        spec.followRedirects = this.followRedirects;
        spec.queryParams.putAll(this.queryParams);
        
        if (values != null && values.length > 0) {
            StringBuilder valueBuilder = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    valueBuilder.append(",");
                }
                valueBuilder.append(values[i].toString());
            }
            spec.queryParams.put(key, valueBuilder.toString());
        }
        return spec;
    }

    /**
     * Returns a new BodyRequestSpecification with the given body object.
     * Copies all other settings from the current specification.
     * @param body the request body object
     * @return a new BodyRequestSpecification
     */
    public RequestSpecification body(Object body) {
        return new BodyRequestSpecification(httpClient, objectMapper, defaultHeaders, baseUrl, body)
                .contentType(this.contentType)
                .urlEncodingEnabled(this.urlEncodingEnabled)
                .redirects().follow(this.followRedirects)
                .queryParams(this.queryParams);
    }

    public RequestSpecification body(File file) {
        return new FileRequestSpecification(httpClient, objectMapper, defaultHeaders, baseUrl, file)
                .contentType(this.contentType)
                .urlEncodingEnabled(this.urlEncodingEnabled)
                .redirects().follow(this.followRedirects)
                .queryParams(this.queryParams);
    }

    public Response get(String url) {
        return executeRequest(() -> {
            HttpUrl httpUrl = buildUrl(url);
            Request request = new Request.Builder()
                    .url(httpUrl)
                    .headers(defaultHeaders.toOkHttpHeaders())
                    .get()
                    .build();
            return httpClient.newCall(request).execute();
        });
    }

    public Response post(String url) {
        return executeRequest(() -> {
            HttpUrl httpUrl = buildUrl(url);
            RequestBody requestBody = RequestBody.create("", MediaType.get(contentType.getContentTypeString()));
            Request request = new Request.Builder()
                    .url(httpUrl)
                    .headers(defaultHeaders.toOkHttpHeaders())
                    .post(requestBody)
                    .build();
            return httpClient.newCall(request).execute();
        });
    }

    public Response delete(String url) {
        return executeRequest(() -> {
            HttpUrl httpUrl = buildUrl(url);
            Request request = new Request.Builder()
                    .url(httpUrl)
                    .headers(defaultHeaders.toOkHttpHeaders())
                    .delete()
                    .build();
            return httpClient.newCall(request).execute();
        });
    }

    private HttpUrl buildUrl(String url) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(url))
                                            .newBuilder();
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            urlBuilder.addQueryParameter(param.getKey(), param.getValue());
        }
        return urlBuilder.build();
    }

    private Response executeRequest(Callable<okhttp3.Response> requestExecutor) {
        try {
            okhttp3.Response okHttpResponse = requestExecutor.call();
            return new Response(okHttpResponse, objectMapper);
        } catch (Exception e) {
            throw new HttpClientException("Failed to execute HTTP request", e);
        }
    }

    public static class RedirectSpecification {
        private final RequestSpecification parent;

        public RedirectSpecification(RequestSpecification parent) {
            this.parent = parent;
        }

        public RequestSpecification follow(boolean follow) {
            // Just set the follow flag on the parent instead of creating a new instance
            parent.followRedirects = follow;
            return parent;
        }
    }

    private class BodyRequestSpecification extends RequestSpecification {
        final Object bodyObject;

        private BodyRequestSpecification(OkHttpClient httpClient, ObjectMapper objectMapper,
                                       Headers defaultHeaders, String baseUrl, Object bodyObject) {
            super(httpClient, objectMapper, defaultHeaders, baseUrl);
            this.bodyObject = bodyObject;
            // Copy state from parent
            this.contentType = RequestSpecification.this.contentType;
            this.urlEncodingEnabled = RequestSpecification.this.urlEncodingEnabled;
            this.followRedirects = RequestSpecification.this.followRedirects;
            this.queryParams.putAll(RequestSpecification.this.queryParams);
        }

        @Override
        public RequestSpecification queryParams(Map<String, String> params) {
            BodyRequestSpecification spec = new BodyRequestSpecification(httpClient, objectMapper, defaultHeaders, baseUrl, bodyObject);
            spec.contentType = this.contentType;
            spec.urlEncodingEnabled = this.urlEncodingEnabled;
            spec.followRedirects = this.followRedirects;
            spec.queryParams.putAll(this.queryParams);
            if (params != null) {
                spec.queryParams.putAll(params);
            }
            return spec;
        }

        @Override
        public Response post(String url) {
            return executeRequest(() -> {
                HttpUrl httpUrl = buildUrl(url);
                String bodyString = bodyObject instanceof String ? 
                        (String) bodyObject : 
                        SerializationUtils.toJson(bodyObject);
                
                RequestBody requestBody = RequestBody.create(bodyString, 
                        MediaType.get(contentType.getContentTypeString()));
                
                // Add Content-Type header
                List<Header> headersList = new ArrayList<>(defaultHeaders.asList());
                headersList.add(new Header("Content-Type", contentType.getContentTypeString()));
                Headers headersWithContentType = new Headers(headersList);
                
                Request request = new Request.Builder()
                        .url(httpUrl)
                        .headers(headersWithContentType.toOkHttpHeaders())
                        .post(requestBody)
                        .build();
                
                return httpClient.newCall(request).execute();
            });
        }
    }

    private class FileRequestSpecification extends RequestSpecification {
        final File file;

        private FileRequestSpecification(OkHttpClient httpClient, ObjectMapper objectMapper,
                                       Headers defaultHeaders, String baseUrl, File file) {
            super(httpClient, objectMapper, defaultHeaders, baseUrl);
            this.file = file;
            // Copy state from parent
            this.contentType = RequestSpecification.this.contentType;
            this.urlEncodingEnabled = RequestSpecification.this.urlEncodingEnabled;
            this.followRedirects = RequestSpecification.this.followRedirects;
            this.queryParams.putAll(RequestSpecification.this.queryParams);
        }

        @Override
        public Response post(String url) {
            return executeRequest(() -> {
                HttpUrl httpUrl = buildUrl(url);
                
                // Detect proper media type based on file extension
                String fileName = file.getName().toLowerCase();
                String mediaType = detectMediaType(fileName);
                
                // Create raw file body with proper Content-Type
                RequestBody requestBody = RequestBody.create(file, MediaType.get(mediaType));
                
                // Add Content-Type header
                List<Header> headersList = new ArrayList<>(defaultHeaders.asList());
                headersList.add(new Header("Content-Type", mediaType));
                Headers headersWithContentType = new Headers(headersList);
                
                Request request = new Request.Builder()
                        .url(httpUrl)
                        .headers(headersWithContentType.toOkHttpHeaders())
                        .post(requestBody)
                        .build();
                
                return httpClient.newCall(request).execute();
            });
        }
        
        @Override
        public RequestSpecification queryParams(Map<String, String> params) {
            FileRequestSpecification spec = new FileRequestSpecification(httpClient, objectMapper, defaultHeaders, baseUrl, file);
            spec.contentType = this.contentType;
            spec.urlEncodingEnabled = this.urlEncodingEnabled;
            spec.followRedirects = this.followRedirects;
            spec.queryParams.putAll(this.queryParams);
            if (params != null) {
                spec.queryParams.putAll(params);
            }
            return spec;
        }
        
        private String detectMediaType(String fileName) {
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (fileName.endsWith(".png")) {
                return "image/png";
            } else if (fileName.endsWith(".gif")) {
                return "image/gif";
            } else if (fileName.endsWith(".webp")) {
                return "image/webp";
            } else if (fileName.endsWith(".mp4")) {
                return "video/mp4";
            } else if (fileName.endsWith(".mov")) {
                return "video/quicktime";
            } else if (fileName.endsWith(".avi")) {
                return "video/x-msvideo";
            } else {
                return "application/octet-stream";
            }
        }
    }
}
