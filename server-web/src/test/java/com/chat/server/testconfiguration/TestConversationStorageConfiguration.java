package com.chat.server.testconfiguration;

import com.chat.server.database.ConversationsDatabase;
import com.chat.server.database.ConversationsStorageFactory;
import com.chat.server.database.FromDatabaseConversationsProvider;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.InMemoryConversationRepository;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.sql.SqlConversationsEngine;
import com.chat.server.sql.SqlConversationsLoader;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@TestConfiguration
public
class TestConversationStorageConfiguration {
    @Bean
    @Primary
    public ConversationStorageFacade testConversationStorageFacade(
            ListUserConversationsFacade listUserConversationsFacade) {
        var facade = new ConversationStorageFacade(new InMemoryConversationRepository());
        facade.addObserver(listUserConversationsFacade.conversationObserver());
        return facade;
    }
}
