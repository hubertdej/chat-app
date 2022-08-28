package com.chat.server.domain.conversationstorage;

public interface ConversationsProvider {
    void provideConversations(ConversationStorageFacade destination);
}
