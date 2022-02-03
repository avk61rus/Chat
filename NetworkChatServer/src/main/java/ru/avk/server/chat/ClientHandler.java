package ru.avk.server.chat;

import ru.avk.clientserver.Command;
import ru.avk.clientserver.CommandType;
import ru.avk.clientserver.commands.AuthCommandData;
import ru.avk.clientserver.commands.PrivateMessageCommandData;
import ru.avk.clientserver.commands.PublicMessageCommandData;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler {
                                                                                            //   Объявляем слудующие поля:
    private final MyServer server;                                                         // -- финализированный сервер;
    private final Socket clientSocket;                                                     // -- финализированный сокет;
    private ObjectInputStream inputStream;                                                 // -- входящий поток на уровне Object;
    private ObjectOutputStream outputStream;                                               // -- исходящий поток на уровне Object;
    private String userName;                                                               // -- имя пользователя;

    public ClientHandler(MyServer server, Socket clientSocket) {                           // * метод обработки получающий на свой вход:
        this.server = server;                                                              //   -- сервер;
        this.clientSocket = clientSocket;                                                  //   --  Сокет клиента;
    }

    public void handle() throws IOException {                                               // * метод
        inputStream = new ObjectInputStream(clientSocket.getInputStream());                 //   - объявление новых входящего и исходящего потоков
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());              //      на сокет клиинта;

        new Thread(() -> {
            try {                                                                            // Производится:
                authenticate();                                                              // -- аутенфикация;
                readMessages();                                                              // -- чтение сообщений;
            } catch (IOException e) {
                System.err.println("Failed to process message from client");                  //   Если аутефикация не произошла и нетсообщения от клиента, то
                e.printStackTrace();                                                          //   выводится:  "Не удалось обработать сообщение от клиента"
            } finally {
                try {                                                                         // Разрывается соединение;
                    closeConnection();
                } catch (IOException e) {                                                     // Если соединение не удалось закрыть выводится  сообщение :
                    System.err.println("Failed to close connection");                         //  "Не удалось закрыть соединение "
                }
            }
        }).start();                                                                           // Старт потоков (если аутетификация прошла)

    }

    private void authenticate() throws IOException {                                                     // * Метод аутентификации
        while (true) {                                                                                   //  бесконечный цикл пока не поступит command;
            Command command = readCommand();
            if (command == null) {
                continue;
            }

            if (command.getType() == CommandType.AUTH) {                                                 // определение типа Command, если тип Command -> AUTH,
                AuthCommandData data = (AuthCommandData) command.getData();                              //  данное имя присваивается в поле username:
                String login = data.getLogin();                                                          //
                String password = data.getPassword();
                String userName = server.getAuthService().getUserNameByLoginAndPassword(login, password);
                if (userName == null) {                                                                   // Проверка на корректность логина и пароля(Object o);
                    sendCommand(Command.errorCommand("Некорректные логин и пароль"));                     //  Проверка существует ли такое имя  в базе;
                } else if (server.isUsernameBusy(userName)) {
                    sendCommand(Command.errorCommand("Такой пользователь уже существует!"));
                } else {
                    this.userName = userName;
                    sendCommand(Command.authOkCommand(userName));                                          // В качестве userName берется имя пользователя;
                    server.subscribe(this);                                                                // ??? куда то пишется
                    return;
                }
            }
        }
    }

    private Command readCommand() throws IOException {
        Command command = null;
        try {
            command = (Command) inputStream.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to read Command class");
//            e.printStackTrace();
        }

        return command;
    }

    private void closeConnection() throws IOException {
        server.unsubscribe(this);                                                                     // ??
        clientSocket.close();
    }

    private void readMessages() throws IOException {
        while (true) {
            Command command = readCommand();
            if (command == null) {
                continue;
            }

            switch (command.getType()) {
                case END:
                    return;
                case PRIVATE_MESSAGE: {
                    PrivateMessageCommandData data = (PrivateMessageCommandData) command.getData();
                    String recipient = data.getReceiver();
                    String privateMessage = data.getMessage();
                    server.sendPrivateMessage(this, recipient, privateMessage);
                    break;
                }
                case PUBLIC_MESSAGE: {
                    PublicMessageCommandData data = (PublicMessageCommandData) command.getData();
                    processMessage(data.getMessage());
                }
            }
        }
    }

    private void processMessage(String message) throws IOException {
        this.server.broadcastMessage(message, this);
    }

    public void sendCommand(Command command) throws IOException {
        outputStream.writeObject(command);
    }

    public String getUserName() {
        return userName;
    }
}
