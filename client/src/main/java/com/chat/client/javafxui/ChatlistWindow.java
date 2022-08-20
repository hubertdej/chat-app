package com.chat.client.javafxui;

import com.chat.client.domain.Chat;
import com.chat.client.presentation.ChatlistPresenter;
import com.chat.client.presentation.ChatlistView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChatlistWindow implements ChatlistView {
    @FXML private Stage stage;
    @FXML private TextField filterTextField;
    @FXML private ListView<Chat> chatsListView;
    @FXML private Button createButton;

    private final ObservableList<Chat> chats = FXCollections.observableArrayList();
    private final FilteredList<Chat> filteredChats = new FilteredList<>(chats, null);

    @Override
    public void initialize(ChatlistPresenter presenter) {
        filterTextField.setOnKeyTyped(event -> presenter.filterChats(filterTextField.getText()));

        chatsListView.setItems(filteredChats);

        chatsListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Chat chat, boolean empty) {
                super.updateItem(chat, empty);
                if (empty || chat == null) {
                    this.setText(null);
                    this.setOnMousePressed(null);
                    return;
                }
                this.setText(chat.getName());
                this.setOnMouseClicked(event -> presenter.openChat(chat));
            }
        });

        chatsListView.setFocusTraversable(false); // chatsListView.addEventFilter(KeyEvent.ANY, Event::consume);

        // TODO: Disable the possibility of opening multiple windows of the same chat.
        createButton.setOnMouseClicked(event -> presenter.createChat());

        stage.setOnCloseRequest(event -> {
            event.consume();
            presenter.close();
        });
    }

    @Override
    public void addChat(Chat chat) {
        chats.add(0, chat);
    }

    @Override
    public void updateChat(Chat chat) {
        chats.remove(chat);
        chats.add(0, chat);
    }

    @Override
    public void filterChats(String filter) {
        filteredChats.setPredicate(chat -> chat.getName().toLowerCase().contains(filter.toLowerCase()));
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
