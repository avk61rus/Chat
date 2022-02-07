package ru.avk.server.chat.auth;

import java.sql.*;

public class PersistentDBAuthService implements IAuthService {

    private static final String DB_URL = "jdbc:sqlite:C:\\IdeaProjects\\Сonversations\\users.db";
    private Connection connection;
    private PreparedStatement getUsernameStatement;
    private PreparedStatement updateUsernameStatement;

    @Override
    public void start() {
        try {
            System.out.println("Create DB connection...");
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("DB connection is created successfully");
            getUsernameStatement = createGetUsernameStatement();
            updateUsernameStatement = createGetUsernameStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.println("Failed to connect to DB by URL: " + DB_URL);
            throw new RuntimeException("Failed to start auth service");
        }
    }

    @Override
    public String getUserNameByLoginAndPassword(String login, String password) {
        String username = null;
        try {
            getUsernameStatement.setString(1, login);
            getUsernameStatement.setString(2, password);
            ResultSet resultSet = getUsernameStatement.executeQuery();
            while (resultSet.next()) {
                username = resultSet.getString("username");
                break;
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.printf("Failed to fetch username to DB. Login: %s%n Password: %s", login, password);
        }
        return username;
    }

    @Override
    public void updateUsername(String currentUsername, String newUsername) {
        try {
            updateUsernameStatement.setString(1, newUsername);
            updateUsernameStatement.setString(2, currentUsername);
            int result = updateUsernameStatement.executeUpdate();
            System.out.println("Update username. Update rows: " + result);
        }catch (SQLException throwables) {
            throwables.printStackTrace();
            System.err.printf("Failed to username. currentUsername: %s; newUsername: %s%n", currentUsername, newUsername);
        }
    }

    private PreparedStatement createGetUsernameStatement() throws SQLException {
        return connection.prepareStatement("SELECT username FROM users WHERE login = ? AND password = ? ");
    }
    private PreparedStatement createUpdateUsernameStatement() throws SQLException {
        return connection.prepareStatement("UPDATE users SET username = ? WHERE username = ? ");
    }
}
