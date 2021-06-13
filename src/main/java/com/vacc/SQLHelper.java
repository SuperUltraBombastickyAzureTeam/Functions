package com.vacc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    private static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(System.getenv("sqlconnection"));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
