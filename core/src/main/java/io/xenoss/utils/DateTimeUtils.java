package io.xenoss.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
    public static final String PATTERN = "YYYY-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static ZonedDateTime getCurrentZonedUtcTime() {
        return LocalDateTime.now()
                            .atZone(ZoneOffset.UTC);
    }

    public static LocalDateTime getCurrentUtcTime() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public static String asString(ZonedDateTime date) {
        return asString(date, PATTERN);
    }

    public static String asString(ZonedDateTime date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(pattern)
                .withZone(ZoneOffset.UTC);
        return date.format(formatter);
    }

    public static String asString(LocalDateTime date) {
        return asString(date, PATTERN);
    }

    public static String asString(LocalDateTime date, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern(pattern)
                .withZone(ZoneOffset.UTC);
        return date.format(formatter);
    }
}
