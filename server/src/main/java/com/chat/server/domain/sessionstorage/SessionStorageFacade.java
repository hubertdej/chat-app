package com.chat.server.domain.sessionstorage;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStorageFacade {
    private final ConversationStorageFacade conversationStorageFacade;
    private final ConcurrentHashMap<String, WebSocketSession> sessions;

    private final ObjectMapper objectMapper;

    public SessionStorageFacade(
            ConversationStorageFacade conversationStorageFacade,
            ConcurrentHashMap<String, WebSocketSession> sessions,
            ObjectMapper objectMapper) {
        this.conversationStorageFacade = conversationStorageFacade;
        this.sessions = sessions;
        this.objectMapper = objectMapper;
    }

    public void add(String username, WebSocketSession session){
        System.out.println("adding session for " + username);
        sessions.put(username, session);
    }
    public void remove(String username){
        System.out.println("removing session of " + username);
        sessions.remove(username);
    }

    public void propagate(MessageDto messageDto) throws NoSuchConversationException, IOException {
        Optional<ConversationDto> conversationDtoOptional = conversationStorageFacade.get(messageDto.getTo());
        if(conversationDtoOptional.isEmpty())
            throw new NoSuchConversationException();
        List<String> members = conversationDtoOptional.get().getMembers();
        for(String member : members){
            WebSocketSession session = sessions.get(member);
            if(session == null)
                continue;
            System.out.println("propagating message to " + member);
            String messageJson = objectMapper.writeValueAsString(messageDto);
            session.sendMessage(new TextMessage(messageJson));
        }

    }


}
