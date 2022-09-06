package com.chat.client.network;

import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.Credentials;
import com.chat.client.domain.MessageFactory;
import com.chat.client.domain.User;
import com.chat.client.domain.application.Dispatcher;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.ChatsUpdater;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

// TODO: Make this implementation safer (reconnecting etc.)
public class WebSocketMessagingClient implements MessagingClient {
    private static final String ADDRESS = "ws://localhost:8080/chat";

    private final User localUser;
    private final Credentials credentials;
    private final Dispatcher dispatcher;
    private final ChatsUpdater chatsUpdater;
    private final ChatsRepository chatsRepository;
    private final MessageFactory messageFactory;

    private final WebSocketClient webSocketClient = new WebSocketClient(URI.create(ADDRESS)) {
        @Override
        public void onOpen(ServerHandshake handshakedata) {
        }

        @Override
        public void onMessage(String message) {
            record MessagePayload(String from, UUID to, String content, Timestamp timestamp) {}

            List<MessagePayload> messagePayloads;
            try {
                messagePayloads = new ObjectMapper().readValue(message, new TypeReference<>() {});
            } catch (JsonProcessingException e) {
                throw new JsonException(e);
            }
            
            messagePayloads.stream()
                    .collect(Collectors.groupingBy(MessagePayload::to, Collectors.toList()))
                    .forEach((chatUUID, payloadList) -> {
                        var messages = payloadList.stream().map(payload ->
                                messageFactory.createMessage(chatUUID, payload.content(), payload.from(), payload.timestamp())
                        ).toList();

                        dispatcher.dispatch(() -> chatsUpdater.handleMessages(chatUUID, chatsRepository, messages));
                    });
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
        }

        @Override
        public void onError(Exception ex) {
        }
    };

    public WebSocketMessagingClient(
            User localUser,
            Credentials credentials,
            ChatsRepository chatsRepository,
            Dispatcher dispatcher,
            ChatsUpdater chatsUpdater,
            MessageFactory messageFactory
    ) {
        this.localUser = localUser;
        this.credentials = credentials;
        this.chatsRepository = chatsRepository;
        this.dispatcher = dispatcher;
        this.chatsUpdater = chatsUpdater;
        this.messageFactory = messageFactory;
    }

    @Override
    public void initialize() {
        record Payload(String from, Map<UUID, Timestamp> lastMessage) {}

        var map = chatsRepository.getChats().stream().collect(
                Collectors.toMap(Chat::getUUID, chat -> chat.getLastMessage().timestamp())
        );
        var payload = new Payload(localUser.name(), map);

        String json;
        try {
            json = new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new JsonException(e);
        }

        webSocketClient.addHeader("username", credentials.username());
        webSocketClient.addHeader("password", credentials.password());
        webSocketClient.addHeader("list-user-conversations-request", json);
        webSocketClient.connect();
    }

    @Override
    public void sendMessage(UUID chatUUID, String text) {
        record MessagePayload(String from, UUID to, String content) {}

        var json = new ObjectMapper().valueToTree(new MessagePayload(localUser.name(), chatUUID, text));

        System.out.println("Sending a message:\n" + json.toPrettyString());
        webSocketClient.send(json.toString());
    }

    @Override
    public void close() {
        webSocketClient.close();
    }
}
