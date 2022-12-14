package com.chat.client.javafxui;

import com.chat.client.presentation.AuthPresenter;
import com.chat.client.presentation.AuthView;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AuthWindow implements AuthView {
    @FXML private Stage stage;
    @FXML private TextField loginUsernameTextField;
    @FXML private PasswordField loginPasswordTextField;
    @FXML private Button loginButton;
    @FXML private TextField registerUsernameTextField;
    @FXML private PasswordField registerPasswordTextField;
    @FXML private Button registerButton;

    @Override
    public void initialize(AuthPresenter presenter) {
        loginButton.setOnMouseClicked(event -> {
            var username = loginUsernameTextField.getText();
            var password = loginPasswordTextField.getText();
            presenter.login(username, password);
        });

        registerButton.setOnMouseClicked(event -> {
            var username = registerUsernameTextField.getText();
            var password = registerPasswordTextField.getText();
            presenter.register(username, password);
        });

        stage.setOnCloseRequest(event -> presenter.close());
    }

    @Override
    public void lockChanges() {
        stage.getScene().getRoot().setDisable(true);
    }

    @Override
    public void unlockChanges() {
        stage.getScene().getRoot().setDisable(false);
    }

    @Override
    public void indicateLoginFailed(String message) {
        new Alert(Alert.AlertType.ERROR, "Login failed. " + message).showAndWait();
    }

    @Override
    public void indicateRegistrationSuccessful() {
        new Alert(Alert.AlertType.INFORMATION, "Registration successful. You can now login.").showAndWait();
    }

    @Override
    public void indicateRegistrationFailed(String message) {
        new Alert(Alert.AlertType.ERROR, "Registration failed. " + message).showAndWait();
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
