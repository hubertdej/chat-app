package com.chat.server.infrastructure.websocket;


import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.listconversationids.dto.ListConversationsRequestDto;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import com.chat.server.infrastructure.rest.IntegrationTest;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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

    private WebSocketSession openSession(String username, String password, BlockingQueue<WebsocketMessageResponse> messages) throws ExecutionException, InterruptedException {
        StandardWebSocketClient client = new StandardWebSocketClient();
        URI uri =URI.create("ws://localhost:" + port + "/chat");
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("username", username);
        headers.add("password", password);
        return client.doHandshake(new ClientSocketHandler(messages), headers, uri).get();
    }

    @Test
    public void testWebsocketListUserConversations() throws Exception {
        registerUser(john, johnPass);
        registerUser(barry, barryPass);
        String uuid = addConversation("johnbarry", List.of(john, barry)).andReturn().getResponse().getContentAsString();
        UUID conversationId = UUID.fromString(uuid);
        MessageDto messageToBarry = new MessageDto(john, conversationId, "hi barry", new Timestamp(System.currentTimeMillis()));
        ListConversationsRequestDto listConversationsRequestDto = new ListConversationsRequestDto(john, Map.of());
        BlockingQueue<WebsocketMessageResponse> johnMessages = new LinkedBlockingQueue<>();
        BlockingQueue<WebsocketMessageResponse> barryMessages = new LinkedBlockingQueue<>();
        try(WebSocketSession johnSession = openSession(john, johnPass, johnMessages);
            WebSocketSession barrySession = openSession(barry, barryPass, barryMessages)){
            johnSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageToBarry)));
            johnSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageToBarry)));
            johnSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(listConversationsRequestDto)));

            WebsocketMessageResponse received = barryMessages.poll(5, TimeUnit.SECONDS);
            barryMessages.poll(5, TimeUnit.SECONDS);
            Assertions.assertEquals(messageToBarry.getContent(), received.getContent());
        }


    }

    @Test
    public void testMessagePropagation() throws Exception {
        registerUser(john, johnPass);
        registerUser(barry, barryPass);
        String uuid = addConversation("johnbarry", List.of(john, barry)).andReturn().getResponse().getContentAsString();
        UUID conversationId = UUID.fromString(uuid);
        MessageDto messageToBarry = new MessageDto(john, conversationId, "hi barry", new Timestamp(System.currentTimeMillis()));
        BlockingQueue<WebsocketMessageResponse> johnMessages = new LinkedBlockingQueue<>();
        BlockingQueue<WebsocketMessageResponse> barryMessages = new LinkedBlockingQueue<>();
        try(WebSocketSession johnSession = openSession(john, johnPass, johnMessages);
            WebSocketSession barrySession = openSession(barry, barryPass, barryMessages)){
            johnSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(messageToBarry)));
            WebsocketMessageResponse received = barryMessages.poll(5, TimeUnit.SECONDS);
            Assertions.assertEquals(messageToBarry.getContent(), received.getContent());
        }
    }

    @Test
    void testListConversationsRequestDeserialization() throws Exception {
        String john = "john";
        String johnPass = "johnpass";
        registerUser(john, johnPass);
        BlockingQueue<WebsocketMessageResponse> johnMessages = new LinkedBlockingQueue<>();
        ListConversationsRequestDto listConversationsRequestDto = new ListConversationsRequestDto("john", null);
        System.out.println(objectMapper.writeValueAsString(listConversationsRequestDto));
        try(WebSocketSession johnSession = openSession(john, johnPass, johnMessages)){
            johnSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(listConversationsRequestDto)));
        }
    }

    private class ClientSocketHandler extends TextWebSocketHandler {
        BlockingQueue<WebsocketMessageResponse> messages;

        public ClientSocketHandler(BlockingQueue<WebsocketMessageResponse> messages) {
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
            WebsocketResponse websocketResponse = objectMapper.readValue(message.getPayload(), WebsocketResponse.class);
            System.out.println(message.getPayload());
            if(websocketResponse instanceof WebsocketMessageResponse websocketMessageResponse){
                messages.add(websocketMessageResponse);
                System.out.println(websocketMessageResponse);
            }
            else if(websocketResponse instanceof WebsocketListConversationsResponse websocketListConversationsResponse){
                System.out.println(websocketListConversationsResponse);
            }

//            messages.add(messageDto);
//            System.out.println(messageDto);
        }
    }

    @JsonTypeInfo(use= JsonTypeInfo.Id.DEDUCTION)
    @JsonSubTypes({@JsonSubTypes.Type(WebsocketListConversationsResponse.class), @JsonSubTypes.Type(WebsocketMessageResponse.class)})
    private static class WebsocketResponse {

    }

    private static class WebsocketListConversationsResponse extends WebsocketResponse{
        private final List<ConversationDto> conversationDtoList;

        @JsonCreator
        public WebsocketListConversationsResponse(
                @JsonProperty("conversations") List<ConversationDto> conversationDtoList) {
            this.conversationDtoList = conversationDtoList;
        }

        public List<ConversationDto> getConversationDtoList() {
            return conversationDtoList;
        }
    }

    private static class WebsocketMessageResponse extends WebsocketResponse {
        private final UUID to;
        private final String content;
        private final Timestamp timestamp;

        @JsonCreator
        public WebsocketMessageResponse(
                @JsonProperty("from") String from,
                @JsonProperty("to") UUID to,
                @JsonProperty("content") String content,
                @JsonProperty("timestamp") Timestamp timestamp) {
            super();
            this.to = to;
            this.content = content;
            this.timestamp = timestamp;
        }

        public UUID getTo() {
            return to;
        }

        public String getContent() {
            return content;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }
    }
}
