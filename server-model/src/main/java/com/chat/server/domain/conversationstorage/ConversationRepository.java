package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.listconversationids.dto.ConversationIdAddedEvent;
import com.chat.server.domain.listconversationids.dto.ConversationIdRemovedEvent;
import com.chat.server.domain.listuserconversations.dto.ConversationAddedEvent;
import com.chat.server.domain.listuserconversations.dto.ConversationRemovedEvent;

import java.util.*;

public interface ConversationRepository {
    void save(Conversation conversation);
    void addMessage(UUID conversationId, Message message) throws NoSuchConversationException;
    void remove(UUID conversationId);
    Optional<Conversation> get(UUID conversationId);
    void addIdObserver(IdObserver observer);
    void addConversationObserver(ConversationObserver observer);
    void removeIdObserver(IdObserver observer);
    void removeConversationObserver(ConversationObserver observer);
    interface IdObserver {
        void notifyAdd(ConversationIdAddedEvent event);
        void notifyRemove(ConversationIdRemovedEvent event);
    }
    interface ConversationObserver {
        void notifyAdd(ConversationAddedEvent event);
        void notifyRemove(ConversationRemovedEvent event);
    }
}
