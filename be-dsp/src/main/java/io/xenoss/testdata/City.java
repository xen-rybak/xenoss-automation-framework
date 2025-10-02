package io.xenoss.testdata;

import lombok.Getter;

@Getter
public enum City {
    LOCAL_CITY("UTC", "127.0.0.1"), // UTC
    SYDNEY("Australia/Sydney", "52.62.100.100"), // UTC+11
    MELBOURNE("Australia/Melbourne", "144.48.38.35"), // UTC+10
    CHICAGO("America/Chicago",  "52.162.161.148"), // UTC-6
    MANGULA("America/Managua", "170.84.132.10"), // UTC-6
    LOS_ANGELES("America/Los_Angeles", "104.174.125.138"); // UTC-7

    private final String timezone;
    private final String ip;

    City(String timezone, String ip) {
        this.timezone = timezone;
        this.ip = ip;
    }
}
