package com.chat.server.testconfiguration;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.InMemoryConversationRepository;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

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
