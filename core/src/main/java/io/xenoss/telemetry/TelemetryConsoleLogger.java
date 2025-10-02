package io.xenoss.telemetry;

import io.xenoss.utils.WaitUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TelemetryConsoleLogger {

    // Static fields
    @Getter
    private static TelemetryConsoleLogger logger;

    // Constructors
    private TelemetryConsoleLogger() {
        new Thread(() -> {
            while (true) {
                WaitUtils.forSeconds(1);
                logToConsole();
            }
        }).start();
    }

    // Static methods
    public static void start() {
        logger = new TelemetryConsoleLogger();
    }

    private void logToConsole() {
        if (TelemetryData.getData()
                         .isEmpty()) {
            return;
        }

        var sb = new StringBuilder();
        sb.append("Telemetry data:");
        for(var record : TelemetryData.getData()
                                      .entrySet()) {
            sb.append(System.lineSeparator())
              .append("\t")
              .append(record.getKey())
              .append(": ")
              .append(record.getValue());
        }
        log.debug(sb.toString());
    }
}
