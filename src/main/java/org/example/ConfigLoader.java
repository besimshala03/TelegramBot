package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static Properties config = null;

    // Lädt die Konfigurationsdatei
    public static String getProperty(String key) {
        if (config == null) {
            loadConfig();
        }
        return config.getProperty(key);
    }

    // Die Konfigurationsdatei laden
    private static void loadConfig() {
        config = new Properties();
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                config.load(inputStream);
            } else {
                throw new RuntimeException("Failed to load configuration file.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration file.", e);
        }
    }
}
