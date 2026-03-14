package com.teenwallet.utils;

import java.sql.*;
import java.io.InputStream;
import java.util.Properties;

public class DBConnection {
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;
    private static final String CONFIG_FILE = "/config.properties";

    static {
        loadConfig();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void loadConfig() {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("Sorry, unable to find config.properties");
                // Set default values
                URL = "jdbc:mysql://localhost:3306/teenwallet?useSSL=false&serverTimezone=UTC";
                USERNAME = "root";
                PASSWORD = "";
                return;
            }
            props.load(input);
            URL = props.getProperty("db.url");
            USERNAME = props.getProperty("db.username");
            PASSWORD = props.getProperty("db.password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.err.println("Database connection failed!");
            e.printStackTrace();
        }
    }
}