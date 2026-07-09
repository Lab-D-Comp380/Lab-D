package com.movieapp;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConfig {

    private static HikariDataSource dataSource;

    private DatabaseConfig() {
    }

    public static synchronized HikariDataSource getDataSource() {
        if (dataSource == null) {
            dataSource = createDataSource();
        }
        return dataSource;
    }

    private static HikariDataSource createDataSource() {
        Properties props = loadProperties();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.user"));
        config.setPassword(props.getProperty("db.password"));
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.size", "5")));
        config.setConnectionTimeout(5_000);
        config.setInitializationFailTimeout(0);

        return new HikariDataSource(config);
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = DatabaseConfig.class.getResourceAsStream("/application.properties")) {
            if (in == null) {
                throw new IllegalStateException("application.properties not found on classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load application.properties", e);
        }
        return props;
    }

    public static boolean testConnection() {
        try (Connection connection = getDataSource().getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            System.err.println("Database connection failed: " + e.getMessage());
            System.err.println("Start MySQL with: docker compose up -d");
            return false;
        }
    }

    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
