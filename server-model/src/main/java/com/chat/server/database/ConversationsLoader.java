package com.chat.server.database;

import java.util.UUID;

public interface ConversationsLoader {
    void readConversationIds(ConversationsEngine.IdsReader reader);
    void readConversation(ConversationsEngine.ConversationReader reader, UUID conversationId);
}
