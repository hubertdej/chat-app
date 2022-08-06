package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.listconversationids.dto.ConversationIdAddedEvent;
import com.chat.server.domain.listconversationids.dto.ConversationIdRemovedEvent;
import com.chat.server.domain.listuserconversations.dto.ConversationAddedEvent;
import com.chat.server.domain.listuserconversations.dto.ConversationRemovedEvent;

import java.util.*;

public class InMemoryConversationRepository implements ConversationRepository {
    Map<UUID, Conversation> storage = new HashMap<>();

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
        Conversation conversation = storage.remove(conversationId);
        return conversation;
    }

    public Optional<Conversation> get(UUID conversationId) {
        return Optional.ofNullable(storage.get(conversationId));
    }
}
