package ru.avk.server.chat;

import ru.avk.clientserver.Command;
import ru.avk.server.chat.auth.AuthService;
import ru.avk.server.chat.auth.IAuthService;
import ru.avk.server.chat.auth.PersistentDBAuthService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final List<ClientHandler> clients = new ArrayList<>();     //-объявлеяем список  клиентов;
    private IAuthService authService;                                   //- объявляем службу идентификации;

    public IAuthService getAuthService() {return authService;}         //- служба получения интендификации;

    public void start(int port) {                                     //- метод запускающий соединение в Try с ресурсами;
        try (ServerSocket serverSocket = new ServerSocket(port)) {    //- создаем сокет
            System.out.println("Server has been started");            //- Сообщаем в консоль о удачном старте сервера;
            authService = createAuthService();                          //- ???? запускаем процесс авторизации из DB;
            authService.start();                                        // вызываем метод старе
            while (true) {
                waitAndProcessClientConnection(serverSocket);           //  ?? не понятно для чего делать бесконечный цикл
            }                                                           //  ?? ели accept все равно дальше не пустит, пока кто
                                                                        //  ?? ни будь не постучится;
        } catch (IOException e) {
            System.err.println("Failed to bind port " + port);
            e.printStackTrace();
        }finally {
            if (authService !=null) {
                authService.stop();
            }
        }
    }

    private  IAuthService createAuthService() {
//        return  new AuthService();
        return new PersistentDBAuthService();
    }
    private void waitAndProcessClientConnection(ServerSocket serverSocket) throws IOException {   // * метод ожидания подключения клиента;
        System.out.println("Waiting for new client connection");                                  // сообщение об ожидании подключения клиента;
        Socket clientSocket = serverSocket.accept();                                              // пока не постучались далее программа не выпоняется;
        System.out.println("Client has been connected");                                          // Сообщение оподключении клиента;
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);                      // ??? на данном сокете интендифицируем  клиента;
        clientHandler.handle();
    }

    public synchronized boolean isUsernameBusy(String username) {                                 // * метод проверки нахождения имени клиента в базе;
        for (ClientHandler client : clients) {
            if (client.getUserName().equals(username)) {                                          // сравнение имени клиента с базой клиентов;
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {                                                              // ?? Если клиент не яляется получателем
            if (client != sender) {                                                                         // отправляется сообщение (message):
                System.out.println("clientMessageCommand");
                client.sendCommand(Command.clientMessageCommand(sender.getUserName(), message));
            }
        }
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String recipient, String privateMessage)
            throws IOException {
        for (ClientHandler client : clients) {                                                               // Если клиент не является отправителем и
            if (client != sender && client.getUserName().equals(recipient)) {                                // при этом получатель - он получает
                client.sendCommand(Command.clientMessageCommand(sender.getUserName(), privateMessage));      // приватное сообщение (privateMessage)
                break;                                                                                       // ? видимо с предложением зарегиться.
            }
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) throws IOException {                      // регистрация нового клинта в базе;
        clients.add(clientHandler);
        notifyClientUserListUpdated();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) throws IOException {
        clients.remove(clientHandler);
        notifyClientUserListUpdated();
    }

    public void notifyClientUserListUpdated() throws IOException {
        List<String> userListOnline = new ArrayList<>();

        for (ClientHandler client : clients) {
            userListOnline.add(client.getUserName());
        }

        for (ClientHandler client : clients) {
            client.sendCommand(Command.updateUserListCommand(userListOnline));
        }
    }
}

