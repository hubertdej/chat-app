package com.chat.server.database;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.ConversationsProvider;
import com.chat.server.domain.conversationstorage.InMemoryConversationRepository;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade.ConversationObserver;

import java.util.List;

public class ConversationsStorageFactory {
    public static ConversationStorageFacade getConversationStorageFacade(
            List<ConversationObserver> observers,
            ConversationsDatabase database,
            ConversationsProvider provider) {
        ConversationStorageFacade facade = new ConversationStorageFacade(
                new InMemoryConversationRepository()
        );
        for (ConversationObserver observer : observers) facade.addObserver(observer);
        provider.provideConversations(facade);
        facade.addObserver(database);
        return facade;
    }
}
