package com.jakub.bone.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties properties = new Properties();

    // Mapping: config.properties key -> environment variable name
    private static final java.util.Map<String, String> ENV_MAPPING = java.util.Map.of(
        "database.host", "DB_HOST",
        "database.port", "DB_PORT",
        "database.name", "DB_NAME",
        "database.user", "DB_USER",
        "database.password", "DB_PASSWORD",
        "server.port", "SERVER_PORT",
        "server.max-clients", "MAX_CLIENTS"
    );

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Configuration file 'config.properties' not found in classpath.");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file.", e);
        }
    }

    /**
     * Gets configuration value. Priority: ENV variable > properties file
     */
    public static String get(String key) {
        // Check if there's an environment variable mapping for this key
        String envKey = ENV_MAPPING.get(key);
        if (envKey != null) {
            String envValue = System.getenv(envKey);
            if (envValue != null && !envValue.isEmpty()) {
                return envValue;
            }
        }
        return properties.getProperty(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static double getDouble(String key) {
        return Double.parseDouble(get(key));
    }
}
