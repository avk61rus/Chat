package ru.avk.server.chat.auth;

public interface IAuthService {
    default void start() {
        //Do nothing;
    }

    default void stop() {
        //Do nothing;
    }

    void updateUsername(String currentUsername, String newUsername);

    String getUserNameByLoginAndPassword(String login, String password);
}


