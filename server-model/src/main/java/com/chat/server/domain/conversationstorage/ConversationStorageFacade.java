package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.ConversationUpdatedEvent;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.conversationstorage.dto.ConversationRemovedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConversationStorageFacade {
    private final ConversationRepository conversationRepository;
    private final List<ConversationObserver> convObservers = new ArrayList<>();

    public ConversationStorageFacade(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }
    public void initialize(ConversationObserver observer) {
        convObservers.add(observer);
    }
    public interface ConversationObserver {
        void notifyUpdate(ConversationUpdatedEvent event);
        void notifyRemove(ConversationRemovedEvent event);
    }

    public UUID add(List<String> members){
        return add(String.join(",", members), members);
    }

    public UUID add(String name, List<String> members){
        Conversation conversation = new Conversation(UUID.randomUUID(), name, members, new ArrayList<>());
        conversationRepository.save(conversation);
        publishConversationUpdatedEvent(conversation, null);
        return conversation.conversationId();
    }

    public void add(UUID conversationId, MessageDto messageDto) throws NoSuchConversationException {
        Message message = new MessageCreator().create(messageDto);
        conversationRepository.addMessage(conversationId, message);
        publishConversationUpdatedEvent(conversationId, messageDto);
    }

    public void remove(UUID conversationId){
        var removedConversation = conversationRepository.remove(conversationId);
        if (removedConversation != null) {
            var conversationRemovedEvent = new ConversationRemovedEvent(conversationId, removedConversation.members());
            for (ConversationObserver o : convObservers) o.notifyRemove(conversationRemovedEvent);
        }
    }

    public Optional<ConversationDto> get(UUID conversationId){
        return conversationRepository.get(conversationId).map(Conversation::dto);
    }

    private void publishConversationUpdatedEvent(UUID conversationId, MessageDto newMessageDto) throws NoSuchConversationException{
        Optional<Conversation> conversationOptional = conversationRepository.get(conversationId);
        if(conversationOptional.isEmpty())
            throw new NoSuchConversationException();
        publishConversationUpdatedEvent(conversationOptional.get(), newMessageDto);
    }
    private void publishConversationUpdatedEvent(Conversation conversation, MessageDto newMessageDto) {
        ConversationUpdatedEvent conversationUpdatedEvent = new ConversationUpdatedEvent(
                conversation.conversationId(),
                conversation.name(),
                conversation.members(),
                newMessageDto
        );
        for (ConversationStorageFacade.ConversationObserver o : convObservers) o.notifyUpdate(conversationUpdatedEvent);
    }
}
