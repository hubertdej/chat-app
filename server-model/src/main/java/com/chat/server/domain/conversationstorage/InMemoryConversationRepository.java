package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryConversationRepository implements ConversationRepository {
    Map<UUID, Conversation> storage = new ConcurrentHashMap<>();

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
        storage.putIfAbsent(conversation.conversationId(), conversation);
    }

    @Override
    public Conversation remove(UUID conversationId) {
        return storage.remove(conversationId);
    }

    public Optional<Conversation> get(UUID conversationId) {
        return Optional.ofNullable(storage.get(conversationId));
    }
}
