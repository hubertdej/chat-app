package com.chat.server.domain.listconversations;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ListConversationsConfiguration {
    @Bean
    ListConversationsFacade listConversationsFacade(ConversationStorageFacade conversationStorageFacade){
        ConversationRepository conversationRepository = new InMemoryConversationRepository();
        return new ListConversationsFacade(conversationRepository, conversationStorageFacade);
    }
}
