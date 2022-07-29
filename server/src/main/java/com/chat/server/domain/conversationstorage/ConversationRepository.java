package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.listconversationids.dto.ConversationIdAddedEvent;
import com.chat.server.domain.listconversationids.dto.ConversationIdRemovedEvent;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.listuserconversations.dto.ConversationAddedEvent;
import com.chat.server.domain.listuserconversations.dto.ConversationRemovedEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;

interface ConversationRepository {
    void save(Conversation conversation);
    void addMessage(UUID conversationId, Message message) throws NoSuchConversationException;
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
    public void addMessage(UUID conversationId, Message message) throws NoSuchConversationException {
        Conversation conversation = storage.get(conversationId);
        if(conversation == null)
            throw new NoSuchConversationException(
                    String.format("couldn't find conversation with id %s", conversationId));
        conversation.messages().add(message);
    }

    @Override
    public void save(Conversation conversation) {
         if(storage.putIfAbsent(conversation.conversationId(), conversation) == null){
             publishConversationIdAddedEvent(conversation);
             publishConversationAddedEvent(conversation);
         }
    }

    private void publishConversationIdAddedEvent(Conversation conversation){
        ConversationIdAddedEvent event = new ConversationIdAddedEvent(
                conversation.conversationId(),
                conversation.name(),
                conversation.members());
        applicationEventPublisher.publishEvent(event);
    }
    private void publishConversationAddedEvent(Conversation conversation){
        ConversationAddedEvent event = new ConversationAddedEvent(
                conversation.conversationId(),
                conversation.name(),
                conversation.members(),
                conversation.messages().stream().map(Message::dto).toList());
        applicationEventPublisher.publishEvent(event);
    }


    @Override
    public void remove(UUID conversationId) {
        Conversation conversation = storage.remove(conversationId);
        if(conversation != null){
            applicationEventPublisher.publishEvent(new ConversationIdRemovedEvent(conversationId, conversation.members()));
            applicationEventPublisher.publishEvent(new ConversationRemovedEvent(conversationId, conversation.members()));
        }
    }

    public Optional<Conversation> get(UUID conversationId){
        return Optional.ofNullable(storage.get(conversationId));
    }
}
