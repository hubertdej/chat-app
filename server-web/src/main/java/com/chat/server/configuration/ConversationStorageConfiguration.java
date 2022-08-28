package com.chat.server.configuration;

import com.chat.server.database.ConversationsDatabase;
import com.chat.server.database.ConversationsLoader;
import com.chat.server.database.ConversationsStorageFactory;
import com.chat.server.domain.conversationstorage.ConversationRepository;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.InMemoryConversationRepository;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.sql.SqlEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public
class ConversationStorageConfiguration {
    @Bean
    public ConversationStorageFacade conversationStorageFacade(
            ListUserConversationsFacade listUserConversationsFacade,
            SqlEngine engine,
            ConversationsLoader loader){
        ConversationRepository conversationRepository = new InMemoryConversationRepository();
        return ConversationsStorageFactory.getConversationStorageFacade(
                List.of(listUserConversationsFacade.conversationObserver()),
                new ConversationsDatabase(engine),
                loader
        );
    }
}
