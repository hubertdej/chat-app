package com.chat.server.domain;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public interface ConversationsEngine {
    interface IdsReader {
        void readId(UUID id);
    }
    interface ConversationReader {
        void readName(String name);
        void readMember(String username);
        void readMessage(String from, UUID to, String content, long timestampValue);
    }
    void addConversation(UUID uuid, String name);
    void addMembers(UUID conversationId, List<String> members);
    void addMessage(String from, UUID to, String content, Timestamp timestamp);

    void removeConversation(UUID conversationId);
    void removeMember(UUID conversationId, String username);
    void readConversationIds(ConversationsEngine.IdsReader reader);
    void readConversation(ConversationsEngine.ConversationReader reader, UUID conversationId);
}
