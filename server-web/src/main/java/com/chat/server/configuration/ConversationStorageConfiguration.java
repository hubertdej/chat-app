package com.chat.server.configuration;

import com.chat.server.database.ConversationsDatabase;
import com.chat.server.database.ConversationsStorageFactory;
import com.chat.server.database.FromDatabaseConversationsProvider;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.sql.SqlConversationsEngine;
import com.chat.server.sql.SqlConversationsLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public
class ConversationStorageConfiguration {
    @Bean
    public ConversationStorageFacade conversationStorageFacade(
            ListUserConversationsFacade listUserConversationsFacade,
            SqlConversationsEngine engine,
            SqlConversationsLoader loader) {
        return new ConversationsStorageFactory().getConversationStorageFacade(
                List.of(listUserConversationsFacade.conversationObserver()),
                new ConversationsDatabase(engine),
                new FromDatabaseConversationsProvider(loader)
        );
    }
}
