package com.chat.server.domain.sessionstorage;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStorageFacade {
    private final ConversationStorageFacade conversationStorageFacade;
    private final ConcurrentHashMap<String, ServerMessagingSession> sessions;

    public SessionStorageFacade(
            ConversationStorageFacade conversationStorageFacade,
            ConcurrentHashMap<String, ServerMessagingSession> sessions) {
        this.conversationStorageFacade = conversationStorageFacade;
        this.sessions = sessions;
    }

    public void add(String username, ServerMessagingSession session){
        System.out.println("adding session for " + username);
        sessions.put(username, session);
    }
    public void remove(String username){
        System.out.println("removing session of " + username);
        sessions.remove(username);
    }

    public void propagate(MessageDto messageDto) throws NoSuchConversationException, MessagingSessionException {
        Optional<ConversationDto> conversationDtoOptional = conversationStorageFacade.get(messageDto.getTo());
        if(conversationDtoOptional.isEmpty())
            throw new NoSuchConversationException();
        List<String> members = conversationDtoOptional.get().getMembers();
        for(String member : members){
            ServerMessagingSession session = sessions.get(member);
            if(session == null)
                continue;
            System.out.println("propagating message to " + member);
            session.sendMessage(messageDto);
        }
    }
}
