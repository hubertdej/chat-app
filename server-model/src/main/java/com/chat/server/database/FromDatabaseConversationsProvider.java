package com.chat.server.database;

import com.chat.server.database.common.ConversationDtoProvider;
import com.chat.server.database.common.ConversationsLoader;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.ConversationsProvider;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;

public class FromDatabaseConversationsProvider implements ConversationsProvider {
    private final ConversationDtoProvider provider;
    private final ConversationsLoader loader;

    public FromDatabaseConversationsProvider(ConversationDtoProvider provider, ConversationsLoader loader) {
        this.provider = provider;
        this.loader = loader;
    }

    @Override
    public void provideConversations(ConversationStorageFacade destination) {
        ConversationsLoader.IdsReader idsReader = id -> {
            var dto = provider.provideDto(loader, id);
            destination.add(dto.getConversationId(), dto.getName(), dto.getMembers());
            for (MessageDto messageDto : dto.getMessages()) {
                try {
                    destination.add(dto.getConversationId(), messageDto);
                } catch (NoSuchConversationException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        loader.readConversationIds(idsReader);
    }
}
