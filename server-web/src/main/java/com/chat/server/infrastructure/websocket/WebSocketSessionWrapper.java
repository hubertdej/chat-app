package com.chat.server.infrastructure.websocket;

import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.messagereceiver.ListConversationsResponse;
import com.chat.server.domain.sessionstorage.MessagingSessionException;
import com.chat.server.domain.sessionstorage.ServerMessagingSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WebSocketSessionWrapper implements ServerMessagingSession {

    private final WebSocketSession inner;
    private final ObjectMapper mapper;
    public WebSocketSessionWrapper(WebSocketSession webSocketSession, ObjectMapper mapper) {
        this.inner = webSocketSession;
        this.mapper = mapper;
    }

    @Override
    public void sendMessage(MessageDto messageDto) throws MessagingSessionException {
        try {
            var messageJson = mapper.writeValueAsString(messageDto);
            inner.sendMessage(new TextMessage(messageJson));
        } catch (IOException e) {
            throw new MessagingSessionException(e);
        }
    }
    @Override
    public void sendMessage(ListConversationsResponse response) throws MessagingSessionException {
        try {
            String jsonResponse = mapper.writeValueAsString(response);
            inner.sendMessage(new TextMessage(jsonResponse));
        } catch (IOException e) {
            throw new MessagingSessionException(e);
        }
    }
}
