package com.chat.server.infrastructure.websocket;


import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.messagereceiver.dto.MessageReceivedDto;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import com.chat.server.infrastructure.rest.IntegrationTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PrivateChatIntegrationTest extends IntegrationTest {

    @Autowired
    MessageReceiverFacade messageReceiverFacade;
    @Autowired
    AuthenticationFacade authenticationFacade;
    @Autowired
    SessionStorageFacade sessionStorageFacade;
    @LocalServerPort
    private int port;

    String john = "john";
    String johnPass = "johnpass";
    String barry = "barry";
    String barryPass = "barrypass";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private WebSocketSession openSession(String username, String password, BlockingQueue<MessageDto> messages) throws ExecutionException, InterruptedException {
        StandardWebSocketClient client = new StandardWebSocketClient();
        URI uri =URI.create("ws://localhost:" + port + "/chat");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("username", username);
        headers.add("password", password);
        headers.add("list-user-conversations-request", String.format("{\"from\": \"%s\", \"lastMessage\": {}}", username));
        return client.doHandshake(new ClientSocketHandler(messages), headers, uri).get();
    }

    @Test
    public void testMessagePropagation() throws Exception {
        registerUser(john, johnPass);
        registerUser(barry, barryPass);
        String uuid = addConversation("johnbarry", List.of(john, barry)).andReturn().getResponse().getContentAsString();
        UUID conversationId = UUID.fromString(uuid);
        MessageReceivedDto messageToBarry = new MessageReceivedDto(john, conversationId, "hi barry");
        BlockingQueue<MessageDto> johnMessages = new LinkedBlockingQueue<>();
        BlockingQueue<MessageDto> barryMessages = new LinkedBlockingQueue<>();
        try(WebSocketSession johnSession = openSession(john, johnPass, johnMessages);
            WebSocketSession barrySession = openSession(barry, barryPass, barryMessages)){
            johnSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageToBarry)));
            MessageDto received = barryMessages.poll(5, TimeUnit.SECONDS);
            Assertions.assertEquals(messageToBarry.content(), received.content());
        }
    }

    @Test
    public void testHistorySendingOnSessionOpen() throws Exception {
        registerUser(john, johnPass);
        registerUser(barry, barryPass);
        String uuid = addConversation("johnbarry", List.of(john, barry)).andReturn().getResponse().getContentAsString();
        UUID conversationId = UUID.fromString(uuid);
        MessageReceivedDto messageToBarry = new MessageReceivedDto(john, conversationId, "hi barry");
        BlockingQueue<MessageDto> johnMessages = new LinkedBlockingQueue<>();
        BlockingQueue<MessageDto> barryMessages = new LinkedBlockingQueue<>();
        try(WebSocketSession johnSession = openSession(john, johnPass, johnMessages)){
            johnSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageToBarry)));
            johnMessages.poll(5, TimeUnit.SECONDS);
            try(WebSocketSession barrySession = openSession(barry, barryPass, barryMessages)){
                MessageDto received = barryMessages.poll(5, TimeUnit.SECONDS);
                Assertions.assertEquals(messageToBarry.content(), received.content());
            }
        }
    }

    private class ClientSocketHandler extends TextWebSocketHandler {
        BlockingQueue<MessageDto> messages;

        public ClientSocketHandler(BlockingQueue<MessageDto> messages) {
            this.messages = messages;
        }

        @Override
        public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
            super.afterConnectionEstablished(session);
            System.out.println("client connected");
        }

        @Override
        public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) throws Exception {
            super.handleTransportError(session, exception);
            exception.printStackTrace();
        }

        @Override
        protected void handleTextMessage(@NotNull WebSocketSession session, TextMessage message) throws Exception {
            List<MessageDto> messageDtos = objectMapper.readValue(message.getPayload(), new TypeReference<>(){});
            messages.addAll(messageDtos);
            System.out.println("Client received: \n" + message.getPayload());
        }
    }
}
