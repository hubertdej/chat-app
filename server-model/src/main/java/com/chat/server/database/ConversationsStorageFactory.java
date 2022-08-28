package com.chat.server.database;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.InMemoryConversationRepository;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade.ConversationObserver;

import java.util.List;

public class ConversationsStorageFactory {
    public static ConversationStorageFacade getConversationStorageFacade(
            List<ConversationObserver> observers,
            ConversationsDatabase database,
            ConversationsLoader loader) {
        ConversationStorageFacade facade = new ConversationStorageFacade(
                new InMemoryConversationRepository()
        );
        for (ConversationObserver observer : observers) facade.addObserver(observer);
        loadConversations(facade, loader);
        facade.addObserver(database);
        return facade;
    }
    private static void loadConversations(ConversationStorageFacade facade,
                                   ConversationsLoader loader) {
        loader.readConversationIds(id -> {
            var reader = new ConversationReader(id);
            loader.readConversation(reader, id);
            var dto = reader.build();
            facade.add(dto.getConversationId(), dto.getName(), dto.getMembers());
            for (MessageDto messageDto : dto.getMessages()) {
                try {
                    facade.add(dto.getConversationId(), messageDto);
                } catch (NoSuchConversationException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
