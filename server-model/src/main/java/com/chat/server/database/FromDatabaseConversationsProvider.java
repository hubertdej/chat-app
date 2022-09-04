package com.chat.server.database;

import com.chat.database.ConversationsLoader;
import com.chat.database.DatabaseConversationProvider;
import com.chat.database.records.DatabaseMessage;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.ConversationsProvider;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;

public class FromDatabaseConversationsProvider implements ConversationsProvider {
    private final DatabaseConversationProvider provider;
    private final ConversationsLoader loader;

    public FromDatabaseConversationsProvider(DatabaseConversationProvider provider, ConversationsLoader loader) {
        this.provider = provider;
        this.loader = loader;
    }

    @Override
    public void provideConversations(ConversationStorageFacade destination) {
        ConversationsLoader.IdsReader idsReader = id -> {
            var dc = provider.provideDatabaseConversation(loader, id);
            destination.add(dc.id(), dc.name(), dc.members());
            for (DatabaseMessage message : dc.messages()) {
                try {
                    destination.add(
                            dc.id(),
                            new MessageDto(message.from(), message.id(), message.content(), message.timestamp())
                    );
                } catch (NoSuchConversationException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        loader.readConversationIds(idsReader);
    }
}
