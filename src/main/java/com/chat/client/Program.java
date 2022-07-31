package com.chat.client;

import com.chat.client.domain.application.AuthService;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.fake.FakeMessagingClient;
import com.chat.client.javafxui.GuiCallbackDispatcher;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.presentation.PresenterFactory;
import com.chat.client.javafxui.Gui;
import com.chat.client.javafxui.WindowFactory;
import com.chat.client.fake.FakeAuthService;
import com.chat.client.presentation.ViewFactory;
import javafx.application.Platform;

class Program {
    public static void main(String[] args) {
        // TODO: Think about moving repository creation to somewhere later in the application lifetime.
        ViewFactory viewFactory = new WindowFactory();
        AuthService authService = new FakeAuthService();
        ChatsRepository chatsRepository = new ChatsRepository();
        CallbackDispatcher callbackDispatcher = new GuiCallbackDispatcher();
        MessagingClient messagingClient = new FakeMessagingClient(Platform::runLater);

        Gui.run(() ->
                new PresenterFactory(
                        viewFactory,
                        authService,
                        chatsRepository,
                        callbackDispatcher,
                        messagingClient
                ).openAuthView()
        );
    }
}
