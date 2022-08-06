package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.listconversationids.dto.ConversationIdAddedEvent;
import com.chat.server.domain.listconversationids.dto.ConversationIdRemovedEvent;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.listuserconversations.dto.ConversationAddedEvent;
import com.chat.server.domain.listuserconversations.dto.ConversationRemovedEvent;

import java.util.*;

public interface ConversationRepository {
    void save(Conversation conversation);
    void addMessage(UUID conversationId, Message message) throws NoSuchConversationException;
    Conversation remove(UUID conversationId);
    Optional<Conversation> get(UUID conversationId);
}

