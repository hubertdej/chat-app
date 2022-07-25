package com.chat.client.fake;

import com.chat.client.domain.*;
import com.chat.client.domain.application.Dispatcher;
import com.chat.client.domain.application.MessagingClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

// TODO:
//  Think whether the WebSocket usage should be abstracted and then moved down the hierarchy
//  and whether more the general logic should be moved up into the domain.
//  Take into account that we want to be able to implement a concrete model.
//  I can think of two approaches:
//    1. Treat the WebSocket usage as an implementation detail of the proxy model.
//    2. Abstract the WebSocket usage to an interface and treat concrete model as an empty implementation.
public class FakeMessagingClient implements MessagingClient {
    private static final String SEPARATOR = "::secret-string::";

    private final Dispatcher dispatcher;
    private Account account;
    private ChatsRepository chatsRepository;

    private final WebSocketClient webSocketClient = new WebSocketClient(URI.create("wss://ws.postman-echo.com/raw")) {
        @Override
        public void onOpen(ServerHandshake handshakedata) {}

        @Override
        public void onMessage(String message) {
            // TODO: Introduce MessageParser class.

            var temp = message.split(SEPARATOR);

            if (temp.length != 3) {
                throw new RuntimeException("Unexpected message format: " + message);
            }

            var sender = temp[0];
            var chatName = temp[1];
            var messageText = temp[2];

            dispatcher.dispatch(() -> {
                // TODO: Implement the case with a non-existent chat.
                chatsRepository.getByName(chatName).get().addMessage(new ChatMessage(messageText, new User(sender)));
            });
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {}

        @Override
        public void onError(Exception ex) {}
    };

    public FakeMessagingClient(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void initialize(Account account, ChatsRepository chatsRepository) {
        this.account = account;
        this.chatsRepository = chatsRepository;
        webSocketClient.connect();
    }

    @Override
    public void sendMessage(Chat chat, ChatMessage message) {
        webSocketClient.send(account.getUser().name() + SEPARATOR + chat.getName() + SEPARATOR + message.text());
    }

    @Override
    public void close() {
        webSocketClient.close();
    }
}
