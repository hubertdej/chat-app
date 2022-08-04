package com.chat.server.configuration;

import com.chat.server.domain.conversationstorage.ConversationRepository;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.InMemoryConversationRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public
class ConversationStorageConfiguration {
    @Bean
    ConversationStorageFacade conversationStorageFacade(){
        ConversationRepository conversationRepository = new InMemoryConversationRepository();
        return new ConversationStorageFacade(conversationRepository);
    }
}
