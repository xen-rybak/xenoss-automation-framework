package io.xenoss.config;

public class ConfigInstance {
    private final ConfigEntity configEntity;

    ConfigInstance(ConfigEntity configEntity) {
        this.configEntity = configEntity;
    }

    public String getEnvironment() {
        return getSystemProperty("environment", configEntity.getEnvironment());
    }

    public Integer getUiActionTimeoutSeconds() {
        return Integer.parseInt(getSystemProperty(
                "uiActionTimeoutSeconds",
                configEntity.getUiActionTimeoutSeconds()));
    }

    public Boolean getIsHeadless() {
        return Boolean.parseBoolean(getSystemProperty(
                "isHeadless",
                configEntity.getIsHeadless()));
    }

    public Boolean getIsSilent() {
        return Boolean.parseBoolean(getSystemProperty(
                "isSilent",
                configEntity.getIsSilent()));
    }

    public Integer getThreadPoolSize() {
        return Integer.parseInt(getSystemProperty(
                "threadPoolSize",
                configEntity.getThreadPoolSize()));
    }

    public Boolean getStartTelemetryServer() {
        return Boolean.parseBoolean(getSystemProperty(
                "startTelemetryServer",
                configEntity.getStartTelemetryServer()));
    }

    public Integer getTelemetryHttpPort() {
        return Integer.parseInt(getSystemProperty(
                "telemetryHttpPort",
                configEntity.getTelemetryHttpPort()));
    }

    public Integer getTelemetryWsPort() {
        return Integer.parseInt(getSystemProperty(
                "telemetryWsPort",
                configEntity.getTelemetryWsPort()));
    }

    public EnvironmentConfig getEnvironmentConfig() {
        var environment = getEnvironment();
        var envConfig = configEntity.getEnvironments()
                                    .get(environment);

        if(envConfig == null) {
            throw new RuntimeException(String.format(
                    "Configuration issue: environment config is not found for '%s'. Check config.yaml",
                    environment));
        }
        return envConfig;
    }

    public static String getSystemProperty(String propertyName, Object defaultValue) {
        return System.getProperty(propertyName) == null
                ? defaultValue.toString()
                : System.getProperty(propertyName);
    }
}
