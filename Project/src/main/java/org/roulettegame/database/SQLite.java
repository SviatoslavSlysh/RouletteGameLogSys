//package org.roulettegame.database;
//
//import org.roulettegame.model.User;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//
//public class SQLite {
//    public static User insertUser(String login, String password) throws SQLException {
//        Connection connection = DatabaseManager.getConnection();
//
//        String insertSQL = "INSERT INTO user (login, password) VALUES (?, ?) RETURNING id, login, password, balance";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
//            preparedStatement.setString(1, login);
//            preparedStatement.setString(2, password);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (resultSet.next()) {
//                    User user = new User();
//                    user.setId(resultSet.getInt("id"));
//                    user.setLogin(resultSet.getString("login"));
//                    user.setPassword(resultSet.getString("password"));
//                    user.setBalance(resultSet.getInt("balance"));
//                    return user;
//                } else {
//                    throw new SQLException("Failed to retrieve the generated user.");
//                }
//            }
//        }
//    }
//
//    public static User validateUser(String login, String password) throws SQLException {
//        Connection connection = DatabaseManager.getConnection();
//
//        String validateUserSql = "SELECT * FROM user WHERE login=? AND password=?";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(validateUserSql)) {
//            preparedStatement.setString(1, login);
//            preparedStatement.setString(2, password);
//
//            return getUser(preparedStatement);
//        }
//    }
//
//    public static User getUserByUsername(String login) {
//        Connection connection = DatabaseManager.getConnection();
//        String validateUserSql = "SELECT * FROM user WHERE login=?";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(validateUserSql)) {
//            preparedStatement.setString(1, login);
//
//            return getUser(preparedStatement);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private static User getUser(PreparedStatement preparedStatement) throws SQLException {
//        try (ResultSet resultSet = preparedStatement.executeQuery()) {
//            if (resultSet.next()) {
//                User user = new User();
//                user.setId(resultSet.getInt("id"));
//                user.setLogin(resultSet.getString("login"));
//                user.setPassword(resultSet.getString("password"));
//                user.setBalance(resultSet.getInt("balance"));
//                return user;
//            } else {
//                // return null or throw an exception
//                return null;
//            }
//        }
//    }
//
//    public static void setUserBalance(Integer userId, Integer balance) throws SQLException {
//        Connection connection = DatabaseManager.getConnection();
//
//        String updateUserBalanceSql = "UPDATE user SET balance=? WHERE id=?";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(updateUserBalanceSql)) {
//            preparedStatement.setInt(1, balance);
//            preparedStatement.setInt(2, userId);
//
//            preparedStatement.executeUpdate();
//        }
//    }
//    public static int getUserBalance(int userId) throws SQLException {
//        Connection connection = DatabaseManager.getConnection();
//        String query = "SELECT balance FROM user WHERE id = ?";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
//            preparedStatement.setInt(1, userId);
//            try (ResultSet resultSet = preparedStatement.executeQuery()) {
//                if (resultSet.next()) {
//                    return resultSet.getInt("balance");
//                } else {
//                    throw new SQLException("User balance not found for user with id: " + userId);
//                }
//            }
//        }
//    }
//}
//


//code with hashpassword
package org.roulettegame.database;

import org.roulettegame.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class SQLite {
//     hashlib method
    private static String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hash);
    }

    public static User insertUser(String login, String password) throws SQLException, NoSuchAlgorithmException {
        Connection connection = DatabaseManager.getConnection();

        String hashedPassword = hashPassword(password); // hash password

        String insertSQL = "INSERT INTO user (login, password) VALUES (?, ?) RETURNING id, login, password, balance";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, hashedPassword); // using hash-pass
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setLogin(resultSet.getString("login"));
                    user.setPassword(resultSet.getString("password"));
                    user.setBalance(resultSet.getInt("balance"));
                    return user;
                } else {
                    throw new SQLException("Failed to retrieve the generated user.");
                }
            }
        }
    }

    public static User validateUser(String login, String password) throws SQLException, NoSuchAlgorithmException {
        Connection connection = DatabaseManager.getConnection();

        String hashedPassword = hashPassword(password);

        String validateUserSql = "SELECT * FROM user WHERE login=? AND password=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(validateUserSql)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, hashedPassword);

            return getUser(preparedStatement);
        }
    }

    public static User getUserByUsername(String login) {
        Connection connection = DatabaseManager.getConnection();
        String validateUserSql = "SELECT * FROM user WHERE login=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(validateUserSql)) {
            preparedStatement.setString(1, login);

            return getUser(preparedStatement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static User getUser(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setLogin(resultSet.getString("login"));
                user.setPassword(resultSet.getString("password"));
                user.setBalance(resultSet.getInt("balance"));
                return user;
            } else {
                return null;
            }
        }
    }

    public static void setUserBalance(Integer userId, Integer balance) throws SQLException {
        Connection connection = DatabaseManager.getConnection();

        String updateUserBalanceSql = "UPDATE user SET balance=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateUserBalanceSql)) {
            preparedStatement.setInt(1, balance);
            preparedStatement.setInt(2, userId);

            preparedStatement.executeUpdate();
        }
    }

    public static int getUserBalance(int userId) throws SQLException {
        Connection connection = DatabaseManager.getConnection();
        String query = "SELECT balance FROM user WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("balance");
                } else {
                    throw new SQLException("User balance not found for user with id: " + userId);
                }
            }
        }
    }
}
