package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.listconversationids.dto.ConversationIdAddedEvent;
import com.chat.server.domain.listconversationids.dto.ConversationIdRemovedEvent;
import com.chat.server.domain.listuserconversations.dto.ConversationAddedEvent;
import com.chat.server.domain.listuserconversations.dto.ConversationRemovedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConversationStorageFacade {
    private final ConversationRepository conversationRepository;

    private final List<IdObserver> idObservers = new ArrayList();
    private final List<ConversationObserver> convObservers = new ArrayList<>();

    public ConversationStorageFacade(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public void addIdObserver(IdObserver observer) {
        idObservers.add(observer);
    }

    public void addConversationObserver(ConversationObserver observer) {
        convObservers.add(observer);
    }

    public void removeIdObserver(IdObserver observer) {
        idObservers.remove(observer);
    }

    public void removeConversationObserver(ConversationObserver observer) {
        convObservers.remove(observer);
    }
    public interface IdObserver {
        void notifyAdd(ConversationIdAddedEvent event);
        void notifyRemove(ConversationIdRemovedEvent event);
    }
    public interface ConversationObserver {
        void notifyAdd(ConversationAddedEvent event);
        void notifyRemove(ConversationRemovedEvent event);
    }

    public UUID add(List<String> members){
        return add(String.join(",", members), members);
    }

    public UUID add(String name, List<String> members){
        Conversation conversation = new Conversation(UUID.randomUUID(), name, members, new ArrayList<>());
        conversationRepository.save(conversation);
        publishConversationIdAddedEvent(conversation);
        publishConversationAddedEvent(conversation);

        return conversation.conversationId();
    }

    public void add(UUID conversationId, MessageDto messageDto) throws NoSuchConversationException {
        Message message = new MessageCreator().create(messageDto);
        conversationRepository.addMessage(conversationId, message);
    }

    public void remove(UUID conversationId){
        var removedConversation = conversationRepository.remove(conversationId);
        if (removedConversation != null) {
            var idRemovedEvent = new ConversationIdRemovedEvent(conversationId, removedConversation.members());
            var conversationRemovedEvent = new ConversationRemovedEvent(conversationId, removedConversation.members());

            for (IdObserver o : idObservers) o.notifyRemove(idRemovedEvent);
            for (ConversationObserver o : convObservers) o.notifyRemove(conversationRemovedEvent);
        }
    }

    public Optional<ConversationDto> get(UUID conversationId){
        return conversationRepository.get(conversationId).map(Conversation::dto);
    }

    private void publishConversationIdAddedEvent(Conversation conversation) {
        ConversationIdAddedEvent event = new ConversationIdAddedEvent(
                conversation.conversationId(),
                conversation.name(),
                conversation.members());
        for (ConversationStorageFacade.IdObserver o : idObservers) o.notifyAdd(event);
    }

    private void publishConversationAddedEvent(Conversation conversation) {
        ConversationAddedEvent event = new ConversationAddedEvent(
                conversation.conversationId(),
                conversation.name(),
                conversation.members(),
                conversation.messages().stream().map(Message::dto).toList());
        for (ConversationStorageFacade.ConversationObserver o : convObservers) o.notifyAdd(event);
    }
}
