package ru.avk.server.chat.auth;

import org.apache.logging.log4j.LogManager;

import java.sql.*;
import org.apache.logging.log4j.Logger;


public class PersistentDBAuthService implements IAuthService {

    private final static Logger LOGGER = LogManager.getLogger(PersistentDBAuthService.class);

    private static final String DB_URL = "jdbc:sqlite:C:\\IdeaProjects\\Сonversations\\users.db";
    private Connection connection;
    private PreparedStatement getUsernameStatement;
    private PreparedStatement updateUsernameStatement;

    @Override
    public void start() {
        try {
            LOGGER.info("Create DB connection...");
            connection = DriverManager.getConnection(DB_URL);
            LOGGER.info("DB connection is created successfully");
            getUsernameStatement = createGetUsernameStatement();
            updateUsernameStatement = createGetUsernameStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            LOGGER.error("Failed to connect to DB by URL: {}" + DB_URL);
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
            LOGGER.error("Failed to fetch username to DB. Login: {} Password: {}%n}", login, password);
        }
        return username;
    }

    @Override
    public void updateUsername(String currentUsername, String newUsername) {
        try {
            updateUsernameStatement.setString(1, newUsername);
            updateUsernameStatement.setString(2, currentUsername);
            int result = updateUsernameStatement.executeUpdate();
           LOGGER.info("Update username. Update rows: " + result);
        }catch (SQLException throwables) {
            throwables.printStackTrace();
           LOGGER.error("Failed to username. currentUsername: {}; newUsername: %{}%n", currentUsername, newUsername);
        }
    }
    @Override
    public void stop() {
        if(connection != null) {
            try {
                LOGGER.info("Closing DB connection");
                connection.close();
                LOGGER.info("DB connection is closed");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                LOGGER.error("Failed to close connection to DB by URL: {}", DB_URL);
                 throw  new RuntimeException("Failed to stop auth service");
            }
        }
    }


    private PreparedStatement createGetUsernameStatement() throws SQLException {
        return connection.prepareStatement("SELECT username FROM users WHERE login = ? AND password = ? ");
    }
    private PreparedStatement createUpdateUsernameStatement() throws SQLException {
        return connection.prepareStatement("UPDATE users SET username = ? WHERE username = ? ");
    }
}
