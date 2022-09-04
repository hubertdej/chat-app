package com.chat.database;

import com.chat.database.records.DatabaseConversation;

import java.util.UUID;

public interface ConversationsLoader {
    interface IdsReader {
        void readId(UUID id);
    }
    interface ConversationReader {
        DatabaseConversation build();
        void readMember(String username);
        void readMessage(String from, String content, long timestampValue);
        void readName(String name);
    }
    void readConversationIds(IdsReader reader);
    void readConversation(ConversationReader reader, UUID conversationId);
}
