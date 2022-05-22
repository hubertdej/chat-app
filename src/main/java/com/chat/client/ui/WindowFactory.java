package com.chat.client.ui;

import com.chat.client.logic.ViewFactory;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class WindowFactory implements ViewFactory {
    private <T> T loadFXML(String name) {
        var loader = new FXMLLoader(ClassLoader.getSystemResource("views/" + name));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loader.getController();
    }

    @Override
    public AuthenticationWindow createAuthenticationView() {
        return loadFXML("AuthenticationView.fxml");
    }

    @Override
    public ConversationWindow createConversationView() { return new ConversationWindow(); }

    @Override
    public LoggedWindow createLoggedView() { return new LoggedWindow(); }
}
