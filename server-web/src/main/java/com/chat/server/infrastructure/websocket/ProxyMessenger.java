package com.chat.server.infrastructure.websocket;

import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;

public class ProxyMessenger implements SessionStorageFacade.Observer {

    private final WebSocketSession session;
    private final ObjectMapper mapper;


    public ProxyMessenger(WebSocketSession session, ObjectMapper mapper) {
        this.session = session;
        this.mapper = mapper;
    }

    @Override
    public void notifyNewMessage(List<MessageDto> messageDtos) {
        try {
            var messageJson = mapper.writeValueAsString(messageDtos);
            session.sendMessage(new TextMessage(messageJson));
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO change?
        }
    }
}
