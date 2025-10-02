package io.xenoss.http;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single HTTP header with a name and value.
 * Used for specifying custom headers in HTTP requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Header {
    /** The name of the header. */
    private String name;
    /** The value of the header. */
    private String value;

    /**
     * Checks if this header matches the given name (case-insensitive).
     * @param name the header name to check
     * @return true if the header name matches, false otherwise
     */
    public boolean hasHeaderWithName(String name) {
        return this.name != null && this.name.equalsIgnoreCase(name);
    }
}
