package com.chat.client;

import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.fake.RestService;
import com.chat.client.fake.WebSocketMessagingClient;
import com.chat.client.javafxui.Gui;
import com.chat.client.javafxui.GuiCallbackDispatcher;
import com.chat.client.javafxui.WindowFactory;
import com.chat.client.presentation.PresenterFactory;
import com.chat.client.presentation.ViewFactory;
import javafx.application.Platform;

class Program {
    private static void open() {
        // TODO: Think about moving repository and client creation to somewhere later in the application lifetime.
        ViewFactory viewFactory = new WindowFactory();
        RestService service = new RestService();
        ChatsRepository chatsRepository = new ChatsRepository();
        CallbackDispatcher callbackDispatcher = new GuiCallbackDispatcher();
        MessagingClient messagingClient = new WebSocketMessagingClient(Platform::runLater);

        new PresenterFactory(
                viewFactory,
                service,
                service,
                service,
                chatsRepository,
                callbackDispatcher,
                messagingClient
        ).openAuthView();
    }

    public static void main(String[] args) {
        Gui.run(() -> {
            open();
            open();
        });
    }
}
