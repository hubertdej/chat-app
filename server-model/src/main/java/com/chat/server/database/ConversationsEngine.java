package com.chat.server.database;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;

import java.util.List;
import java.util.UUID;

public interface ConversationsEngine {
    interface IdsReader {
        void readId(UUID id);
    }
    interface ConversationReader {
        ConversationDto build();
        void readMember(String username);
        void readMessage(String from, UUID to, String content, long timestampValue);
        void readName(String name);
    }
    void addConversation(UUID uuid, String name);
    void addMembers(UUID conversationId, List<String> members);
    void addMessage(String from, UUID to, String content, long timestamp);

    void removeConversation(UUID conversationId);
    void removeMember(UUID conversationId, String username);
}
