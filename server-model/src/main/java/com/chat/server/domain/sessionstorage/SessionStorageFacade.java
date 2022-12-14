package com.chat.server.domain.sessionstorage;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStorageFacade {
    private final ConversationStorageFacade conversationStorageFacade;


    public SessionStorageFacade(
            ConversationStorageFacade conversationStorageFacade) {
        this.conversationStorageFacade = conversationStorageFacade;
    }

    public interface Observer {
        void notifyNewMessage(List<MessageDto> dto);
    }

    private final Map<String, List<Observer>> observers = new ConcurrentHashMap<>();
    public void addObserver(String username, Observer observer) {
        observers.putIfAbsent(username, new ArrayList<>());
        observers.get(username).add(observer);
    }
    public void removeObserver(String username, Observer observer) {
        observers.getOrDefault(username, new ArrayList<>()).remove(observer);
    }

    public void propagate(MessageDto messageDto) {
        Optional<ConversationDto> conversationDtoOptional = conversationStorageFacade.get(messageDto.to());
        if (conversationDtoOptional.isEmpty())
            throw new RuntimeException(new NoSuchConversationException());

        List<String> members = conversationDtoOptional.get().getMembers();
        for (String member : members) {
            for (var observer : observers.getOrDefault(member, List.of())) {
                observer.notifyNewMessage(List.of(messageDto));
            }
        }
    }
}
