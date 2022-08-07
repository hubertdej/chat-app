package com.chat.client.fake;

import com.chat.client.domain.*;
import com.chat.client.domain.application.Dispatcher;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.utils.ChatsUpdater;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.List;
import java.util.UUID;

// TODO: Make this implementation safer (reconnecting etc.)
public class WebSocketMessagingClient implements MessagingClient {
    private static final String ADDRESS = "ws://localhost:8080/chat";

    private final Dispatcher dispatcher;
    private ChatsUpdater updater;
    private Account account;
    private ChatsRepository chatsRepository;

    private record MessagePayload(
       String from,
       UUID to,
       String content,
       long timestamp
    ) {}

    private final WebSocketClient webSocketClient = new WebSocketClient(URI.create(ADDRESS)) {
        @Override
        public void onOpen(ServerHandshake handshakedata) {
        }

        @Override
        public void onMessage(String message) {

            MessagePayload payload;
            try {
                payload = new ObjectMapper().readValue(message, MessagePayload.class);
            } catch (JsonProcessingException e) {
                throw new JsonException(e);
            }

            dispatcher.dispatch(() -> {
                updater.handleMessage(
                        payload.to(),
                        chatsRepository,
                        new ChatMessage(payload.content(), new User(payload.from())));
            });
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
        }

        @Override
        public void onError(Exception ex) {
        }
    };

    public WebSocketMessagingClient(Dispatcher dispatcher, ChatsUpdater updater) {
        this.dispatcher = dispatcher;
        this.updater = updater;
    }

    @Override
    public void initialize(Account account, ChatsRepository chatsRepository) {
        this.account = account;
        this.chatsRepository = chatsRepository;

        // TODO: Send a token.
        webSocketClient.addHeader("username", account.getUsername());
        webSocketClient.addHeader("password", account.getPassword());
        webSocketClient.connect();
    }

    @Override
    public void sendMessage(Chat chat, ChatMessage message) {
        var json = new ObjectMapper().valueToTree(new MessagePayload(
                account.getUsername(),
                chat.getUUID(),
                message.text(),
                System.currentTimeMillis()
        ));

        System.out.println("Sending a message:\n" + json.toPrettyString());
        webSocketClient.send(json.toString());
    }

    @Override
    public void close() {
        webSocketClient.close();
    }
}
