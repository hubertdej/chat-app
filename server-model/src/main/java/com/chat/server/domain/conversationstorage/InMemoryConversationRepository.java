package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.listconversationids.dto.ConversationIdAddedEvent;
import com.chat.server.domain.listconversationids.dto.ConversationIdRemovedEvent;
import com.chat.server.domain.listuserconversations.dto.ConversationAddedEvent;
import com.chat.server.domain.listuserconversations.dto.ConversationRemovedEvent;

import java.util.*;

public class InMemoryConversationRepository implements ConversationRepository {
    Map<UUID, Conversation> storage = new HashMap<>();

    List<IdObserver> idObservers = new ArrayList();
    List<ConversationObserver> convObservers = new ArrayList<>();

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

    @Override
    public void addMessage(UUID conversationId, Message message) throws NoSuchConversationException {
        Conversation conversation = storage.get(conversationId);
        if (conversation == null)
            throw new NoSuchConversationException(
                    String.format("couldn't find conversation with id %s", conversationId));
        conversation.messages().add(message);
    }

    @Override
    public void save(Conversation conversation) {
        if (storage.putIfAbsent(conversation.conversationId(), conversation) == null) {
            publishConversationIdAddedEvent(conversation);
            publishConversationAddedEvent(conversation);
        }
    }

    private void publishConversationIdAddedEvent(Conversation conversation) {
        ConversationIdAddedEvent event = new ConversationIdAddedEvent(
                conversation.conversationId(),
                conversation.name(),
                conversation.members());
        for (IdObserver o : idObservers) o.notifyAdd(event);
    }

    private void publishConversationAddedEvent(Conversation conversation) {
        ConversationAddedEvent event = new ConversationAddedEvent(
                conversation.conversationId(),
                conversation.name(),
                conversation.members(),
                conversation.messages().stream().map(Message::dto).toList());
        for (ConversationObserver o : convObservers) o.notifyAdd(event);
    }
    @Override
    public void remove(UUID conversationId) {
        Conversation conversation = storage.remove(conversationId);
        if (conversation != null) {
            var idRemovedEvent = new ConversationIdRemovedEvent(conversationId, conversation.members());
            var conversationRemovedEvent = new ConversationRemovedEvent(conversationId, conversation.members());

            for (IdObserver o : idObservers) o.notifyRemove(idRemovedEvent);
            for (ConversationObserver o : convObservers) o.notifyRemove(conversationRemovedEvent);
        }
    }
    public Optional<Conversation> get(UUID conversationId) {
        return Optional.ofNullable(storage.get(conversationId));
    }
}
