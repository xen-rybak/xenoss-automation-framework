package io.xenoss.http;

import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Represents a collection of HTTP headers for requests and responses.
 * Provides utility methods for header management and conversion.
 */
@Data
public class Headers implements Iterable<Header> {
    /** The list of headers in this collection. */
    private final List<Header> headers;

    /**
     * Constructs an empty Headers collection.
     */
    public Headers() {
        this.headers = new ArrayList<>();
    }

    /**
     * Constructs a Headers collection from an array of Header objects.
     * @param headers the headers to include
     */
    public Headers(Header... headers) {
        this.headers = new ArrayList<>(Arrays.asList(headers));
    }

    /**
     * Constructs a Headers collection from a list of Header objects.
     * @param headers the headers to include
     */
    public Headers(List<Header> headers) {
        this.headers = new ArrayList<>(headers);
    }

    /**
     * Constructs a Headers collection from a map of header names and values.
     * @param headerMap the map of header names and values
     */
    public Headers(Map<String, String> headerMap) {
        this.headers = headerMap.entrySet().stream()
                .map(entry -> new Header(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Returns a copy of the headers as a list.
     * @return a list of Header objects
     */
    public List<Header> asList() {
        return new ArrayList<>(headers);
    }

    /**
     * Checks if a header with the given name exists in the collection.
     * @param name the header name to check
     * @return true if a header with the name exists, false otherwise
     */
    public boolean hasHeaderWithName(String name) {
        return headers.stream().anyMatch(h -> h.hasHeaderWithName(name));
    }

    /**
     * Returns the first header with the given name, or null if not found.
     * @param name the header name to search for
     * @return the Header object, or null if not found
     */
    public Header get(String name) {
        return headers.stream()
                .filter(h -> h.hasHeaderWithName(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the value of the first header with the given name, or null if not found.
     * @param name the header name to search for
     * @return the header value, or null if not found
     */
    public String getValue(String name) {
        Header header = get(name);
        return header != null ? header.getValue() : null;
    }

    /**
     * Returns a new Headers collection with the given header added.
     * @param header the header to add
     * @return a new Headers object with the added header
     */
    public Headers add(Header header) {
        List<Header> newHeaders = new ArrayList<>(this.headers);
        newHeaders.add(header);
        return new Headers(newHeaders);
    }

    /**
     * Converts this Headers collection to OkHttp Headers.
     * Only headers with non-null names and values are included.
     * @return an OkHttp Headers object
     */
    public okhttp3.Headers toOkHttpHeaders() {
        okhttp3.Headers.Builder builder = new okhttp3.Headers.Builder();
        for (Header header : headers) {
            if (header.getName() != null && header.getValue() != null) {
                builder.add(header.getName(), header.getValue());
            }
        }
        return builder.build();
    }

    /**
     * Returns an iterator over the headers in this collection.
     * @return an Iterator of Header objects
     */
    @NotNull
    @Override
    public Iterator<Header> iterator() {
        return headers.iterator();
    }
}
