package com.chat.server.infrastructure.websocket;

import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import com.chat.server.infrastructure.websocket.dto.MissingHeaderException;
import com.chat.server.infrastructure.websocket.dto.WebSocketMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class WebSocketHandler extends TextWebSocketHandler {
    private final static String USERNAME_HEADER = "username";
    private final static String PASSWORD_HEADER = "password";
    private final static ObjectMapper objectMapper = new ObjectMapper();
    private final MessageReceiverFacade messageReceiverFacade;
    private final AuthenticationFacade authenticationFacade;
    private final SessionStorageFacade sessionStorageFacade;


    public WebSocketHandler(MessageReceiverFacade messageReceiverFacade, AuthenticationFacade authenticationFacade, SessionStorageFacade sessionStorageFacade) {
        this.messageReceiverFacade = messageReceiverFacade;
        this.authenticationFacade = authenticationFacade;
        this.sessionStorageFacade = sessionStorageFacade;
    }

    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, TextMessage textMessage) throws IOException, NoSuchConversationException {
        System.out.println("server received: " + textMessage.getPayload());
        WebSocketMessageDto webSocketMessageDto = objectMapper.readValue(textMessage.getPayload(), WebSocketMessageDto.class);
        messageReceiverFacade.receiveMessage(session, webSocketMessageDto);
    }

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws IOException {
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
        if(username == null)
            throw new MissingHeaderException("username header is missing");
        return username;
    }

    private String getPasswordHeader(WebSocketSession session){
        String password = session.getHandshakeHeaders().getFirst(PASSWORD_HEADER);
        if(password == null)
            throw new MissingHeaderException("password header is missing");
        return password;
    }
}
