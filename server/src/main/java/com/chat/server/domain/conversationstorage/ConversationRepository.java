package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.ConversationAddedEvent;
import com.chat.server.domain.conversationstorage.dto.ConversationRemovedEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;

interface ConversationRepository {
    void save(Conversation conversation);
    void remove(UUID conversationId);
    Optional<Conversation> get(UUID conversationId);
}

class InMemoryConversationRepository implements ConversationRepository {
    Map<UUID, Conversation> storage = new HashMap<>();
    ApplicationEventPublisher applicationEventPublisher;

    InMemoryConversationRepository(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void save(Conversation conversation) {
         if(storage.putIfAbsent(conversation.conversationId(), conversation) == null)
             publishConversationAddedEvent(conversation);
    }

    void publishConversationAddedEvent(Conversation conversation){
        ConversationAddedEvent event = new ConversationAddedEvent(
                conversation.conversationId(),
                conversation.name(),
                conversation.members());
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void remove(UUID conversationId) {
        Conversation conversation = storage.remove(conversationId);
        if(conversation != null)
            applicationEventPublisher.publishEvent(new ConversationRemovedEvent(conversationId, conversation.members()));
    }

    public Optional<Conversation> get(UUID conversationId){
        return Optional.ofNullable(storage.get(conversationId));
    }
}
