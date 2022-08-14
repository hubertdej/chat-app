package com.chat.client.javafxui;


import com.chat.client.domain.ChatMessage;
import com.chat.client.presentation.ChatPresenter;
import com.chat.client.presentation.ChatView;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ChatWindow implements ChatView {
    @FXML private Stage stage;
    @FXML private ListView<ChatMessage> messagesListView;
    @FXML private TextArea messageTextArea;
    @FXML private Button sendButton;

    @Override
    public void initialize(ChatPresenter presenter) {
        messagesListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(ChatMessage message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    this.setAlignment(null);
                    this.setText(null);
                    return;
                }
                var alignment = message.sentByLocalUser() ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT;
                this.setAlignment(alignment);
                this.setText(message.sender().name() + ": " + message.text());
            }
        });

        sendButton.setOnMouseClicked(event -> {
            var text = messageTextArea.getText();
            messageTextArea.clear();
            presenter.sendMessage(text);
        });
    }

    @Override
    public void addMessage(ChatMessage message) {
        messagesListView.getItems().add(message);
    }

    @Override
    public void setTitle(String title) {
        stage.setTitle(title);
    }

    @Override
    public void open() {
        stage.show();
    }

    @Override
    public void close() {
        stage.close();
    }
}
