package io.xenoss.config;

import io.xenoss.exceptions.ConfigurationException;

public class ConfigInstance {
    private final ConfigEntity configEntity;

    ConfigInstance(ConfigEntity configEntity) {
        this.configEntity = configEntity;
    }

    public String getEnvironment() {
        return getSystemProperty("environment", configEntity.getEnvironment());
    }

    public Integer getUiActionTimeoutSeconds() {
        String value = getSystemProperty(
                "uiActionTimeoutSeconds",
                configEntity.getUiActionTimeoutSeconds());
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Invalid value for uiActionTimeoutSeconds: " + value, e);
        }
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
        String value = getSystemProperty(
                "threadPoolSize",
                configEntity.getThreadPoolSize());
        try {
            int poolSize = Integer.parseInt(value);
            if (poolSize <= 0) {
                throw new ConfigurationException("threadPoolSize must be positive, got: " + poolSize);
            }
            return poolSize;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Invalid value for threadPoolSize: " + value, e);
        }
    }

    public Boolean getStartTelemetryServer() {
        return Boolean.parseBoolean(getSystemProperty(
                "startTelemetryServer",
                configEntity.getStartTelemetryServer()));
    }

    public Integer getTelemetryHttpPort() {
        String value = getSystemProperty(
                "telemetryHttpPort",
                configEntity.getTelemetryHttpPort());
        try {
            int port = Integer.parseInt(value);
            validatePort(port, "telemetryHttpPort");
            return port;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Invalid value for telemetryHttpPort: " + value, e);
        }
    }

    public Integer getTelemetryWsPort() {
        String value = getSystemProperty(
                "telemetryWsPort",
                configEntity.getTelemetryWsPort());
        try {
            int port = Integer.parseInt(value);
            validatePort(port, "telemetryWsPort");
            return port;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Invalid value for telemetryWsPort: " + value, e);
        }
    }

    private void validatePort(int port, String paramName) {
        if (port < 1 || port > 65535) {
            throw new ConfigurationException(
                    String.format("%s must be between 1 and 65535, got: %d", paramName, port));
        }
    }

    public EnvironmentConfig getEnvironmentConfig() {
        var environment = getEnvironment();
        var envConfig = configEntity.getEnvironments()
                                    .get(environment);

        if(envConfig == null) {
            throw new ConfigurationException(String.format(
                    "Environment configuration not found for '%s'. Check testConfig.yaml",
                    environment));
        }
        return envConfig;
    }

    public Integer getTelemetryUpdateIntervalMs() {
        String value = getSystemProperty(
                "telemetryUpdateIntervalMs",
                configEntity.getTelemetryUpdateIntervalMs() != null
                        ? configEntity.getTelemetryUpdateIntervalMs()
                        : 100);
        try {
            int interval = Integer.parseInt(value);
            if (interval < 10) {
                throw new ConfigurationException("telemetryUpdateIntervalMs must be at least 10ms, got: " + interval);
            }
            return interval;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Invalid value for telemetryUpdateIntervalMs: " + value, e);
        }
    }

    public Integer getHttpConnectionPoolSize() {
        String value = getSystemProperty(
                "httpConnectionPoolSize",
                configEntity.getHttpConnectionPoolSize() != null
                        ? configEntity.getHttpConnectionPoolSize()
                        : 100);
        try {
            int size = Integer.parseInt(value);
            if (size <= 0) {
                throw new ConfigurationException("httpConnectionPoolSize must be positive, got: " + size);
            }
            return size;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Invalid value for httpConnectionPoolSize: " + value, e);
        }
    }

    public Integer getHttpTimeoutSeconds() {
        String value = getSystemProperty(
                "httpTimeoutSeconds",
                configEntity.getHttpTimeoutSeconds() != null
                        ? configEntity.getHttpTimeoutSeconds()
                        : 30);
        try {
            int timeout = Integer.parseInt(value);
            if (timeout <= 0) {
                throw new ConfigurationException("httpTimeoutSeconds must be positive, got: " + timeout);
            }
            return timeout;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Invalid value for httpTimeoutSeconds: " + value, e);
        }
    }

    public Integer getHttpKeepAliveSeconds() {
        String value = getSystemProperty(
                "httpKeepAliveSeconds",
                configEntity.getHttpKeepAliveSeconds() != null
                        ? configEntity.getHttpKeepAliveSeconds()
                        : 30);
        try {
            int keepAlive = Integer.parseInt(value);
            if (keepAlive <= 0) {
                throw new ConfigurationException("httpKeepAliveSeconds must be positive, got: " + keepAlive);
            }
            return keepAlive;
        } catch (NumberFormatException e) {
            throw new ConfigurationException("Invalid value for httpKeepAliveSeconds: " + value, e);
        }
    }

    public static String getSystemProperty(String propertyName, Object defaultValue) {
        return System.getProperty(propertyName) == null
                ? defaultValue.toString()
                : System.getProperty(propertyName);
    }
}
