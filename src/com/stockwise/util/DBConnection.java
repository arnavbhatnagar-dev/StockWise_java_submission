package com.stockwise.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Path CONFIG_FILE =
            Path.of("config", "database.properties");

    public static Connection getConnection() throws SQLException {
        Properties properties = loadProperties();
        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        );
    }

    private static Properties loadProperties() throws SQLException {
        if (!Files.exists(CONFIG_FILE)) {
            throw new SQLException(
                    "Database configuration not found. Copy "
                            + "config/database.properties.example to "
                            + "config/database.properties and update it."
            );
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            throw new SQLException("Unable to read database configuration.", e);
        }

        if (isBlank(properties.getProperty("db.url"))
                || isBlank(properties.getProperty("db.user"))
                || properties.getProperty("db.password") == null) {
            throw new SQLException("Database configuration is incomplete.");
        }
        return properties;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
