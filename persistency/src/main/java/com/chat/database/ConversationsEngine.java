package com.chat.database;

import java.util.List;
import java.util.UUID;

public interface ConversationsEngine {
    void addConversation(UUID uuid, String name);
    void addMembers(UUID conversationId, List<String> members);
    void addMessage(String from, UUID to, String content, long timestamp);

    void removeConversation(UUID conversationId);
    void removeMember(UUID conversationId, String username);
}
