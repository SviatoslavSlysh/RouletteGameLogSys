package org.roulettegame.database;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String JDBC_URL = "jdbc:sqlite:database.db";
    @Getter
    private static final Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(JDBC_URL);
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish a database connection.", e);
        }
    }

    private static void createTables() throws SQLException {
        try (Statement statement = DatabaseManager.connection.createStatement()) {
            String createUser = "CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT, password TEXT, balance INTEGER NOT NULL DEFAULT 0)";
            statement.executeUpdate(createUser);
        }
    }
}