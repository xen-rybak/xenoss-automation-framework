package io.xenoss.config;

import io.xenoss.utils.SerializationUtils;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Thread-safe configuration manager using Initialization-on-demand holder idiom.
 * This pattern ensures thread-safe lazy initialization without synchronization overhead.
 */
@Slf4j
public class ConfigurationManager {
    private static final String PATH_CONFIG_YAML = System.getProperty("config.path", "../testConfig.yaml");

    /**
     * Private constructor to prevent instantiation.
     */
    private ConfigurationManager() {
        throw new AssertionError("ConfigurationManager should not be instantiated");
    }

    /**
     * Holder class for ConfigEntity using Initialization-on-demand holder idiom.
     * The inner class is not loaded until the getConfigEntity() method is called,
     * ensuring thread-safe lazy initialization without explicit synchronization.
     */
    private static class ConfigEntityHolder {
        private static final ConfigEntity INSTANCE = parseTestConfigYaml();
    }

    /**
     * Returns the singleton ConfigEntity instance.
     * Thread-safe without synchronization overhead.
     *
     * @return the ConfigEntity instance
     */
    private static ConfigEntity getConfigEntity() {
        return ConfigEntityHolder.INSTANCE;
    }

    /**
     * Holder class for ConfigInstance using Initialization-on-demand holder idiom.
     */
    private static class ConfigInstanceHolder {
        private static final ConfigInstance INSTANCE = new ConfigInstance(getConfigEntity());
    }

    /**
     * Returns the singleton ConfigInstance.
     * Thread-safe without synchronization overhead thanks to the holder idiom.
     *
     * @return the ConfigInstance
     */
    public static ConfigInstance getConfig() {
        return ConfigInstanceHolder.INSTANCE;
    }

    private static ConfigEntity parseTestConfigYaml() {
        final Yaml yaml = new Yaml();

        final String defaultFileName = PATH_CONFIG_YAML;

        log.debug("Locating configuration file, \"{}\"...", defaultFileName);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(defaultFileName), StandardCharsets.UTF_8))) {
            log.debug("Configuration file, \"{}\" found. Parsing yaml file...", defaultFileName);
            var configData = yaml.load(reader);
            var jsonElement = SerializationUtils.toJsonTree(configData);

            return SerializationUtils.fromJson(jsonElement, ConfigEntity.class);

        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
