package com.healthsphere.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/healthsphere_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "admin";

    public static Connection getConnection() {
        try {
            // Un-comment when integrating real MySQL/PostgreSQL DB
            // return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            return null; 
        } catch (Exception e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
            return null;
        }
    }

    public static boolean checkConnectionStatus() {
        // Mocking active DB connectivity for demo UI
        return true; 
    }
}