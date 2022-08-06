package com.chat.server.infrastructure.websocket;

import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.listuserconversations.dto.ListConversationsRequestDto;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import com.chat.server.infrastructure.websocket.dto.MissingHeaderException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final static String USERNAME_HEADER = "username";
    private final static String PASSWORD_HEADER = "password";
    private final static String LIST_CONVERSATIONS_REQUEST_HEADER = "list-user-conversations-request";
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final MessageReceiverFacade messageReceiverFacade;
    private final AuthenticationFacade authenticationFacade;
    private final SessionStorageFacade sessionStorageFacade;
    private final ListUserConversationsFacade listUserConversationsFacade;

    public WebSocketHandler(
            MessageReceiverFacade messageReceiverFacade,
            AuthenticationFacade authenticationFacade,
            SessionStorageFacade sessionStorageFacade,
            ListUserConversationsFacade listUserConversationsFacade) {
        this.messageReceiverFacade = messageReceiverFacade;
        this.authenticationFacade = authenticationFacade;
        this.sessionStorageFacade = sessionStorageFacade;
        this.listUserConversationsFacade = listUserConversationsFacade;
    }

    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, TextMessage textMessage) throws IOException, NoSuchConversationException {
        System.out.println("server received: " + textMessage.getPayload());
        MessageDto messageDto = objectMapper.readValue(textMessage.getPayload(), MessageDto.class);
        messageReceiverFacade.receiveMessage(session, messageDto);
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws IOException {
        authenticate(session);
        sendConversationHistory(session);
    }

    private void authenticate(WebSocketSession session) throws IOException {
        String username = getUsernameHeader(session);
        String password = getPasswordHeader(session);
        if(authenticationFacade.authenticate(username, password)){
            System.out.printf("user %s successfully authenticated%n", username);
            sessionStorageFacade.add(username, session);
        } else {
            System.out.printf("user %s couldn't be authenticated", username);
            session.close();
        }
    }

    private void sendConversationHistory(WebSocketSession session) throws IOException {
        ListConversationsRequestDto listConversationsRequestDto = getListConversationsRequestDto(session);
        List<ConversationDto> conversationDtoList = listUserConversationsFacade.listConversations(listConversationsRequestDto);
        for(ConversationDto conversationDto : conversationDtoList)
            for(MessageDto messageDto : conversationDto.getMessages()){
                String messageJson = objectMapper.writeValueAsString(messageDto);
                session.sendMessage(new TextMessage(messageJson));
            }
//        ListConversationsResponse listConversationsResponse = new ListConversationsResponse(conversationDtoList);
//        String jsonResponse = objectMapper.writeValueAsString(listConversationsResponse);
//        session.sendMessage(new TextMessage(jsonResponse));
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) {
        String username = getUsernameHeader(session);
        sessionStorageFacade.remove(username);
        System.out.println("connection closed " + status);
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session, Throwable exception) {
        exception.printStackTrace();
    }

    private String getUsernameHeader(WebSocketSession session){
        String username = session.getHandshakeHeaders().getFirst(USERNAME_HEADER);
        if(username == null || username.isEmpty())
            throw new MissingHeaderException("username header is missing");
        return username;
    }

    private String getPasswordHeader(WebSocketSession session){
        String password = session.getHandshakeHeaders().getFirst(PASSWORD_HEADER);
        if(password == null || password.isEmpty())
            throw new MissingHeaderException("password header is missing");
        return password;
    }

    private ListConversationsRequestDto getListConversationsRequestDto(WebSocketSession session) throws JsonProcessingException {
        String json = session.getHandshakeHeaders().getFirst(LIST_CONVERSATIONS_REQUEST_HEADER);
        if(json == null || json.isEmpty())
            throw new MissingHeaderException(LIST_CONVERSATIONS_REQUEST_HEADER + "is missing");
        return objectMapper.readValue(json, ListConversationsRequestDto.class);
    }
}
