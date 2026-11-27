package com.qa.agent.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Reads test configuration from {@code src/test/resources/config.properties}.
 * A JVM system property with the same key (e.g. {@code -Dbase.url=...}) overrides the file value.
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = load();

    private ConfigReader() {
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException(CONFIG_FILE + " not found on the test classpath");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + CONFIG_FILE, e);
        }
        return properties;
    }

    public static String get(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing config value for key: " + key);
        }
        return value.trim();
    }

    /** Base URL without a trailing slash, e.g. https://www.saucedemo.com */
    public static String baseUrl() {
        String url = get("base.url");
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public static String validUsername() {
        return get("valid.username");
    }

    public static String lockedUsername() {
        return get("locked.username");
    }

    public static String problemUsername() {
        return get("problem.username");
    }

    public static String password() {
        return get("password");
    }
}
