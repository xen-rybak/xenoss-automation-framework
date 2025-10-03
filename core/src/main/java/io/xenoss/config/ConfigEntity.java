package io.xenoss.config;

import lombok.Getter;

import java.util.Map;

public class ConfigEntity {
    @Getter
    String environment;
    @Getter
    Integer uiActionTimeoutSeconds;
    @Getter
    Boolean isHeadless;
    @Getter
    Boolean isSilent;
    @Getter
    Boolean startTelemetryServer;
    @Getter
    Integer threadPoolSize;
    @Getter
    Integer telemetryHttpPort;
    @Getter
    Integer telemetryWsPort;
    @Getter
    Integer telemetryUpdateIntervalMs;
    @Getter
    Integer httpConnectionPoolSize;
    @Getter
    Integer httpTimeoutSeconds;
    @Getter
    Integer httpKeepAliveSeconds;
    Map<String, EnvironmentConfig> environments;

    public Map<String, EnvironmentConfig> getEnvironments() {
        return environments == null ? null : Map.copyOf(environments);
    }
}
