package com.chat.server.domain.conversationstorage;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
class ConversationStorageConfiguration {
    @Bean
    ConversationStorageFacade conversationStorageFacade(ApplicationEventPublisher applicationEventPublisher){
        ConversationRepository conversationRepository = new InMemoryConversationRepository(applicationEventPublisher);
        return new ConversationStorageFacade(conversationRepository);
    }
}
