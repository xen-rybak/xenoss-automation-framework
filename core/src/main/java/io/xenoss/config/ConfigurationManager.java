package io.xenoss.config;

import io.xenoss.exceptions.ConfigurationException;
import io.xenoss.utils.SerializationUtils;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.FileInputStream;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Thread-safe configuration manager using Initialization-on-demand holder idiom.
 * This pattern ensures thread-safe lazy initialization without synchronization overhead.
 */
@Slf4j
public class ConfigurationManager {
    private static final String DEFAULT_CONFIG_NAME = "testConfig.yaml";

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
        final String configPath = locateConfigFile();

        log.info("Loading configuration from: {}", configPath);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(configPath), StandardCharsets.UTF_8))) {
            log.debug("Parsing configuration file...");
            var configData = yaml.load(reader);
            var jsonElement = SerializationUtils.toJsonTree(configData);

            return SerializationUtils.fromJson(jsonElement, ConfigEntity.class);

        } catch (IOException ex) {
            throw new ConfigurationException("Failed to load configuration from: " + configPath, ex);
        }
    }

    /**
     * Locates the configuration file by checking multiple locations in order:
     * 1. System property 'config.path' (absolute path)
     * 2. Current working directory
     * 3. Parent directory (for backward compatibility)
     * 4. Project root directory (searches up to 5 levels)
     *
     * @return absolute path to the configuration file
     * @throws RuntimeException if configuration file cannot be found
     */
    private static String locateConfigFile() {
        // 1. Check system property first (highest priority)
        String configPath = System.getProperty("config.path");
        if (configPath != null && new File(configPath).exists()) {
            return configPath;
        }

        // 2. Check current working directory
        String currentDir = System.getProperty("user.dir");
        File currentDirFile = new File(currentDir, DEFAULT_CONFIG_NAME);
        if (currentDirFile.exists()) {
            return currentDirFile.getAbsolutePath();
        }

        // 3. Check parent directory (backward compatibility)
        File parentDirFile = new File(currentDir, "../" + DEFAULT_CONFIG_NAME);
        if (parentDirFile.exists()) {
            return parentDirFile.getAbsolutePath();
        }

        // 4. Search up to 5 levels to find project root
        File searchDir = new File(currentDir);
        for (int i = 0; i < 5; i++) {
            File configFile = new File(searchDir, DEFAULT_CONFIG_NAME);
            if (configFile.exists()) {
                return configFile.getAbsolutePath();
            }
            searchDir = searchDir.getParentFile();
            if (searchDir == null) {
                break;
            }
        }

        // Configuration not found
        throw new ConfigurationException(String.format(
                """
                        Configuration file '%s' not found. Searched locations:
                          1. System property 'config.path': %s
                          2. Current directory: %s
                          3. Parent directory: %s
                          4. Up to 5 parent levels from: %s
                        Please ensure the configuration file exists or set -Dconfig.path=<absolute-path>""",
                DEFAULT_CONFIG_NAME,
                System.getProperty("config.path", "not set"),
                currentDirFile.getAbsolutePath(),
                parentDirFile.getAbsolutePath(),
                currentDir
        ));
    }
}
