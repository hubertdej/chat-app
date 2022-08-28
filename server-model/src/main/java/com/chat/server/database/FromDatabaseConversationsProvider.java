package com.chat.server.database;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.ConversationsProvider;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;

public class FromDatabaseConversationsProvider implements ConversationsProvider {
    private final ConversationsLoader loader;

    public FromDatabaseConversationsProvider(ConversationsLoader loader) {
        this.loader = loader;
    }

    @Override
    public void provideConversations(ConversationStorageFacade destination) {
        loader.readConversationIds(id -> {
            var reader = new ConversationReader(id);
            loader.readConversation(reader, id);
            var dto = reader.build();
            destination.add(dto.getConversationId(), dto.getName(), dto.getMembers());
            for (MessageDto messageDto : dto.getMessages()) {
                try {
                    destination.add(dto.getConversationId(), messageDto);
                } catch (NoSuchConversationException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
