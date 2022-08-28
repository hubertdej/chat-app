package com.chat.server.database;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.InMemoryConversationRepository;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;

public class ConversationsStorageFactory {
    public ConversationStorageFacade getConversationStorageFacade(ConversationsDatabase database,
                                                                  ConversationsLoader loader,
                                                                  ConversationReader reader) {
        ConversationStorageFacade facade = new ConversationStorageFacade(
                new InMemoryConversationRepository()
        );
        loadConversations(facade, loader, reader);
        facade.addObserver(database);
        return facade;
    }
    private void loadConversations(ConversationStorageFacade facade,
                                   ConversationsLoader loader,
                                   ConversationReader reader) {
        loader.readConversationIds(id -> {
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
