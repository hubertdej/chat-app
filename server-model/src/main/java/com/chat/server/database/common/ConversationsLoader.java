package com.chat.server.database.common;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;

import java.util.UUID;

public interface ConversationsLoader {
    interface IdsReader {
        void readId(UUID id);
    }
    interface ConversationReader {
        ConversationDto build();
        void readMember(String username);
        void readMessage(String from, UUID to, String content, long timestampValue);
        void readName(String name);
    }
    void readConversationIds(IdsReader reader);
    void readConversation(ConversationReader reader, UUID conversationId);
}
