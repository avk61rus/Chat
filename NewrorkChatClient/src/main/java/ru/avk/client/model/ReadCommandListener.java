package ru.avk.client.model;

import ru.avk.clientserver.Command;

public interface ReadCommandListener {

    void processReceivedCommand(Command command);

}