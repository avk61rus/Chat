package ru.avk.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import ru.avk.client.ClientChat;
import ru.avk.client.model.Network;
import ru.avk.client.model.ReadCommandListener;
import ru.avk.client.model.dialogs.Dialogs;
import ru.avk.client.service.ChatHistory;
import ru.avk.clientserver.Command;
import ru.avk.clientserver.CommandType;
import ru.avk.clientserver.commands.ClientMessageCommandData;
import ru.avk.clientserver.commands.UpdateUserListCommandData;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Optional;

public class ClientController {

    private static final int LAST_HISTOTY_ROWS_NUMBER = 100;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    @FXML
    private Button sendButton;
    @FXML
    public ListView<String> userList;

    private ClientChat application;
    private ChatHistory chatHistoryService;

    public void createChatHistory() {
        this.chatHistoryService = new ChatHistory(Network.getInstance().getCurrentUserName());
        chatHistoryService.init();
    }

    public void sendMessage() {
        String message = textField.getText().trim();
        if (message.isEmpty()) {
            textField.clear();
            return;
        }
        String sender = null;
        if (!userList.getSelectionModel().isEmpty()) {
            sender = userList.getSelectionModel().getSelectedItem();
        }
        try {
            if (sender != null) {
                Network.getInstance().sendPrivateMessage(sender, message);
            } else {
                System.out.println("ClientController Network.getInstance().sendMessage(message);");
                Network.getInstance().sendMessage(message);
            }
        } catch (IOException e) {
            application.showErrorDialog("Ошибка передачи данных по сети");
        }
        appendMessageToChat("Я", message);
    }

    private void appendMessageToChat(String sender, String message) {
        String currentText = textArea.getText();
        textArea.appendText(DateFormat.getDateTimeInstance().format(new Date()));
        textArea.appendText(System.lineSeparator());
        if (sender != null) {
            textArea.appendText(sender + ":");
            textArea.appendText(System.lineSeparator());
        }
        textArea.appendText(message);
        textArea.appendText(System.lineSeparator());
        textArea.appendText(System.lineSeparator());
        textField.setFocusTraversable(true);
        textField.clear();
        String newMessage = textArea.getText(currentText.length(), textArea.getLength());
        chatHistoryService.appendText(newMessage);
    }

    public void setApplication(ClientChat application) {
        this.application = application;
    }

    public void initializeMessageHandler() {
        Network.getInstance().addReadMessageListener(new ReadCommandListener() {
            @Override
            public void processReceivedCommand(Command command) {
                if (chatHistoryService == null) {
                    createChatHistory();
                    loadChatHistory();
                }
                if (command.getType() == CommandType.CLIENT_MESSAGE) {
                    ClientMessageCommandData data = (ClientMessageCommandData) command.getData();
                    appendMessageToChat(data.getSender(), data.getMessage());
                } else if (command.getType() == CommandType.UPDATE_USER_LIST) {
                    UpdateUserListCommandData data = (UpdateUserListCommandData) command.getData();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            userList.setItems(FXCollections.observableList(data.getUsers()));
                        }
                    });
                }
            }
        });
    }

    public void updateUserName(ActionEvent actionEvent) {
        TextInputDialog editDialog = new TextInputDialog();
        editDialog.setTitle("Изменить имя пользователя");
        editDialog.setHeaderText("Введите новое имя пользователя");
        editDialog.setContentText("Username: ");
        Optional<String> result = editDialog.showAndWait();
        if (result.isPresent()) {
            try {
                Network.getInstance().changeUsername(result.get());
            } catch (IOException e) {
                e.printStackTrace();
                Dialogs.NetworkError.SEND_MESSAGE.show();
            }
        }
    }

    public void close(ActionEvent actionEvent) {
        chatHistoryService.close();
        ClientChat.INSTANCE.getChatStage().close();
    }

    public void about(ActionEvent actionEvent) {
        Dialogs.AboutDialog.INFO.show();
    }

    private void loadChatHistory() {
        String rows = chatHistoryService.loadLastRow(LAST_HISTOTY_ROWS_NUMBER);
        textArea.clear();
        textArea.setText(rows);
    }

    public void showHelp(ActionEvent actionEvent) throws URISyntaxException, IOException {
        Desktop desktop = Desktop.getDesktop();
        desktop.browse(new URI("https://faq.whatsapp.com/"));
    }
}