package ru.avk.server.chat.auth;

public interface IAuthService {
    default void start() {
    }

    default void stop() {
    }

    void updateUsername(String currentUsername, String newUsername);

    String getUserNameByLoginAndPassword(String login, String password);
}


