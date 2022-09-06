package com.chat.server.infrastructure.websocket;

import com.chat.server.domain.messagereceiver.dto.ListConversationsResponse;
import com.chat.server.domain.sessionstorage.ConversationsRequester;
import com.chat.server.domain.sessionstorage.MessagingSessionException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WebConversationsRequester implements ConversationsRequester {

    private final WebSocketSession inner;
    private final ObjectMapper mapper;
    public WebConversationsRequester(WebSocketSession webSocketSession, ObjectMapper mapper) {
        this.inner = webSocketSession;
        this.mapper = mapper;
    }

    @Override
    public void forwardMessage(ListConversationsResponse response) throws MessagingSessionException {
        try {
            String jsonResponse = mapper.writeValueAsString(response);
            inner.sendMessage(new TextMessage(jsonResponse));
        } catch (IOException e) {
            throw new MessagingSessionException(e);
        }
    }
}
