package com.chat.server.configuration;

import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class SessionStorageConfiguration {
    @Bean
    public SessionStorageFacade sessionStorageFacade(ConversationStorageFacade conversationStorageFacade){
        return new SessionStorageFacade(conversationStorageFacade, new ConcurrentHashMap<>());
    }
}
