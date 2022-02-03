package ru.avk.server;

import ru.avk.server.chat.MyServer;

public class ServerApp {

    private static final int DEFAULT_PORT = 8189;     //назначаем порт;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        new MyServer().start(port);                  //стартуем сервер с прослушиваемым поортом 8189;
    }
}
