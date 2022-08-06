package com.chat.server.configuration;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.listconversationids.ConversationIdRepository;
import com.chat.server.domain.listconversationids.InMemoryConversationIdRepository;
import com.chat.server.domain.listconversationids.ListConversationIdsFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public
class ListConversationIdsConfiguration {
    @Bean
    public ListConversationIdsFacade listConversationIdsFacade(ConversationStorageFacade conversationStorageFacade){
        ConversationIdRepository conversationIdRepository = new InMemoryConversationIdRepository();
        return new ListConversationIdsFacade(conversationIdRepository, conversationStorageFacade);
    }
}
