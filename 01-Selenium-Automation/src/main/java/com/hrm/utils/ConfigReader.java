package com.hrm.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigReader - Singleton utility to read configuration from config.properties.
 *
 * <p>Supports multiple config files and system property overrides.
 * Priority: System Property > config.properties
 *
 * @author Deep Ghevariya
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config.properties";

    static {
        loadProperties();
    }

    private ConfigReader() {
        // Utility class - prevent instantiation
    }

    private static void loadProperties() {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.warn("{} not found on classpath. Using system properties only.", CONFIG_FILE);
                return;
            }
            properties.load(input);
            logger.info("Configuration loaded from: {}", CONFIG_FILE);
            logger.debug("Properties count: {}", properties.size());
        } catch (IOException e) {
            logger.error("Failed to load configuration file: {}", CONFIG_FILE, e);
            throw new RuntimeException("Cannot load configuration: " + CONFIG_FILE, e);
        }
    }

    /**
     * Get property value, checking system properties first.
     *
     * @param key property key
     * @return property value
     * @throws RuntimeException if property not found
     */
    public static String get(String key) {
        // System property takes priority (allows CI/CD override)
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isEmpty()) {
            logger.debug("Property '{}' resolved from System property", key);
            return sysProp;
        }
        String value = properties.getProperty(key);
        if (value == null) {
            logger.error("Property '{}' not found in {} or system properties", key, CONFIG_FILE);
            throw new RuntimeException("Missing configuration property: " + key);
        }
        return value.trim();
    }

    /**
     * Get property value with default fallback.
     *
     * @param key          property key
     * @param defaultValue fallback value if not found
     * @return property value or default
     */
    public static String get(String key, String defaultValue) {
        try {
            return get(key);
        } catch (RuntimeException e) {
            logger.debug("Property '{}' not found, using default: '{}'", key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Get property as integer.
     */
    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(get(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Get property as boolean.
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        try {
            return Boolean.parseBoolean(get(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /** Reload properties from disk (useful for test isolation) */
    public static void reload() {
        properties.clear();
        loadProperties();
    }
}
