package com.example.inventory.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Properties;

public class DatabaseClient {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseClient() {
        this(loadProperties());
    }

    DatabaseClient(Properties properties) {
        this.url = Objects.requireNonNull(properties.getProperty("db.url"), "db.url property is required");
        this.user = properties.getProperty("db.user", "");
        this.password = properties.getProperty("db.password", "");
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream in = DatabaseClient.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) {
                throw new IllegalStateException("application.properties not found on classpath");
            }
            properties.load(in);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load application.properties", e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (user.isBlank() && password.isBlank()) {
            return DriverManager.getConnection(url);
        }
        return DriverManager.getConnection(url, user, password);
    }

    public void initializeSchema() {
        String ddl = "CREATE TABLE IF NOT EXISTS devices (" +
                "id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, " +
                "type TEXT NOT NULL, " +
                "location TEXT NOT NULL, " +
                "serial_number TEXT NOT NULL UNIQUE, " +
                "description TEXT, " +
                "redistributable BOOLEAN NOT NULL DEFAULT FALSE" +
                ")";

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate(ddl);
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to initialize database schema", e);
        }
    }
}
