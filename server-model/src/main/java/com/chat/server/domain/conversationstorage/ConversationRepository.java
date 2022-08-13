package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;

import java.util.*;

public interface ConversationRepository {
    void save(Conversation conversation);
    void addMessage(UUID conversationId, Message message) throws NoSuchConversationException;
    Conversation remove(UUID conversationId);
    Optional<Conversation> get(UUID conversationId);
}

