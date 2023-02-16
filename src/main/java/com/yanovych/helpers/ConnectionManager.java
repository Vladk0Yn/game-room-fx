package com.yanovych.helpers;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Slf4j
public class ConnectionManager {
    private String CONNECTION_URL;
    private String USER;
    private String PASSWORD;
    private static ConnectionManager instance;

    private ConnectionManager() {
        readConnectionProperties();
    }

    public static ConnectionManager getConnectionManager() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    private void readConnectionProperties() {
        try {
            Properties properties = PropertiesManager.getProperties("project.properties");
            this.CONNECTION_URL = properties.getProperty("url");
            this.USER = properties.getProperty("user");
            this.PASSWORD = properties.getProperty("password");
        } catch (IOException e) {
            log.error("Properties file not found");
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
    }
}
