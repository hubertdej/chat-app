package com.chat.client.javafxui;

import com.chat.client.presentation.AuthenticationPresenter;
import com.chat.client.presentation.AuthenticationView;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AuthenticationWindow implements AuthenticationView {
    @FXML private HBox root;
    @FXML private TextField loginUsernameTextField;
    @FXML private PasswordField loginPasswordTextField;
    @FXML private Button loginButton;
    @FXML private TextField registerUsernameTextField;
    @FXML private PasswordField registerPasswordTextField;
    @FXML private Button registerButton;

    private final Stage stage = new Stage();

    @Override
    public void initialize(AuthenticationPresenter presenter) {
        stage.setScene(new Scene(root));

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
    }

    @Override
    public void indicateLoginFailed() {
        new Alert(Alert.AlertType.ERROR, "Login failed.").showAndWait();
    }

    @Override
    public void indicateRegistrationSuccessful() {
        new Alert(Alert.AlertType.INFORMATION, "Registration successful. You can now login.").showAndWait();
    }

    @Override
    public void indicateRegistrationFailed() {
        new Alert(Alert.AlertType.ERROR, "Registration failed.").showAndWait();
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
