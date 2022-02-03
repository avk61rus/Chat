package ru.avk.server.chat.auth;

import java.util.Set;

public class AuthService implements IAuthService {

    private static final Set<User> USERS = Set.of(
            new User("login1", "pass1", "username1"),
            new User("login2", "pass2", "username2"),
            new User("login3", "pass3", "username3")
    );

    public String getUserNameByLoginAndPassword(String login, String password) {    // * метод региструрующий нового User и принимающий на вход логин и пароль;
        User requiredUser = new User(login, password);                              // создается экземпляр юзера с логином и паролем;
        for (User user : USERS) {                                                   // проверка на совпадение логина и пароля с базой, если совпадений нет, то
            if (requiredUser.equals(user)) {                                         // возвращается имя юзера;
                return user.getUserName();
            }
        }

        return null;
    }

    @Override
    public void updateUsername(String currentUsername, String newUsername) {
        User user = getUserByUsermame(currentUsername);
        if (user != null) {
            user.setUsername(newUsername);
        }
    }

//    @Override // добавлено мною;
//    public String detUserNameByLoginAndPassword(String login, String password) {
//        return null;
//    }

    private User getUserByUsermame(String username) {
        for (User user : USERS) {
            if (user.getUserName().equals(username)) {
                return user;
            }
        }
        return null;
    }

}
