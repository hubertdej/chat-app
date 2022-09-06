package com.chat.client.javafxui;


import com.chat.client.domain.Message;
import com.chat.client.domain.User;
import com.chat.client.presentation.ChatPresenter;
import com.chat.client.presentation.ChatView;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.util.List;

public class ChatWindow implements ChatView {
    private String titleBase = "";
    @FXML private Stage stage;
    @FXML private ListView<Message> messagesListView;
    @FXML private TextArea messageTextArea;
    @FXML private Button sendButton;

    @Override
    public void initialize(ChatPresenter presenter) {
        messagesListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    this.setGraphic(null);
                    return;
                }

                var textFlow = new TextFlow();

                var senderText = new Text(message.sender().name());
                senderText.setFill(message.sentByLocalUser() ? Color.MEDIUMSEAGREEN : Color.MEDIUMPURPLE);

                var messageText = new Text(message.text());

                var timestampText = new Text(message.timestamp().toString());
                timestampText.setStyle("-fx-font-size: 8");
                timestampText.setFill(Color.GREY);

                textFlow.getChildren().addAll(
                        senderText,
                        new Text("\n"),
                        messageText,
                        new Text("\n"),
                        timestampText
                );
                this.setGraphic(textFlow);
            }
        });

        sendButton.setOnMouseClicked(event -> {
            var text = messageTextArea.getText();
            messageTextArea.clear();
            presenter.sendMessage(text);
        });

        stage.setOnCloseRequest(event -> presenter.close());
    }

    @Override
    public void addMessage(Message message) {
        messagesListView.getItems().add(message);
    }

    @Override
    public void setTitle(String title) {
        titleBase = title;
        stage.setTitle(title);
    }

    @Override
    public void displayChatMembers(List<User> members) {
        stage.setTitle(titleBase + " (" +  String.join(", ", members.stream().map(User::name).sorted().toList()) + ")");
    }

    @Override
    public void open() {
        stage.show();
    }

    @Override
    public void close() {
        stage.close();
    }

    @Override
    public void focus() {
        stage.requestFocus();
    }
}
