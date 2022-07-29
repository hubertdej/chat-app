package com.chat.server.domain.listconversationids;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ListConversationIdsConfiguration {
    @Bean
    ListConversationIdsFacade listConversationIdsFacade(ConversationStorageFacade conversationStorageFacade){
        ConversationIdRepository conversationIdRepository = new InMemoryConversationIdRepository();
        return new ListConversationIdsFacade(conversationIdRepository, conversationStorageFacade);
    }
}
