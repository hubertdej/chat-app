package com.chat.client.javafxui;

import com.chat.client.presentation.AuthView;
import com.chat.client.presentation.ViewFactory;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class WindowFactory implements ViewFactory {
    private <T> T loadFXML(Class<T> cls) {
        var loader = new FXMLLoader(ClassLoader.getSystemResource("views/" + cls.getSimpleName() + ".fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            throw new FXMLException(e);
        }
        return loader.getController();
    }

    @Override
    public AuthView createAuthView() {
        return loadFXML(AuthView.class);
    }

    @Override
    public ConversationWindow createConversationView() { return new ConversationWindow(); }

    @Override
    public LoggedWindow createLoggedView() { return new LoggedWindow(); }
}
