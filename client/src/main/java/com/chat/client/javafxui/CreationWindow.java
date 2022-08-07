package com.chat.client.javafxui;

import com.chat.client.domain.User;
import com.chat.client.presentation.CreationPresenter;
import com.chat.client.presentation.CreationView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class CreationWindow implements CreationView {
    @FXML private Stage stage;
    @FXML private TextField filterTextField;
    @FXML private ListView<User> usersListView;
    @FXML private HBox userHBox;
    @FXML private Button createButton;
    @FXML private Button cancelButton;

    private CreationPresenter presenter;
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final FilteredList<User> filteredUsers = new FilteredList<>(users, null);

    public void initialize(CreationPresenter presenter) {
        this.presenter = presenter;

        filterTextField.setOnKeyTyped(event -> presenter.filterUsers(filterTextField.getText()));

        usersListView.setItems(filteredUsers);

        usersListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    this.setText(null);
                    this.setOnMouseClicked(null);
                    return;
                }
                this.setText(user.name());
                this.setOnMouseClicked(event -> presenter.selectUser(user));
            }
        });

        // TODO: Add a TextField for a chat name.
        createButton.setOnMouseClicked(event -> presenter.createChat("[chat name]"));

        cancelButton.setOnMouseClicked(event -> presenter.close());
    }

    @Override
    public void addUser(User user) {
        users.add(user);
    }

    @Override
    public void filterUsers(String filter) {
        filteredUsers.setPredicate(user -> user.name().toLowerCase().contains(filter.toLowerCase()));
    }

    @Override
    public void selectUser(User user) {
        var button = new Button(user.name() + " ⓧ");
        button.setId(user.name());
        button.setOnMouseClicked(event -> presenter.unselectUser(user));
        userHBox.getChildren().add(button);
    }

    @Override
    public void unselectUser(User user) {
        userHBox.getChildren().removeIf(button -> button.getId().equals(user.name()));
    }

    @Override
    public void enableCreation() {
        createButton.setDisable(false);
    }

    @Override
    public void disableCreation() {
        createButton.setDisable(true);
    }

    @Override
    public void indicateLoadingUsersFailed() {
        new Alert(Alert.AlertType.ERROR, "Couldn't load users.").showAndWait();
    }

    @Override
    public void indicateChatCreationFailed() {
        new Alert(Alert.AlertType.ERROR, "Unable to create chat.").showAndWait();
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
