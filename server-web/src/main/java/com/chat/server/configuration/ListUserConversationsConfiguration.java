package com.chat.server.configuration;

import com.chat.server.domain.listuserconversations.InMemoryUserConversationRepository;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class ListUserConversationsConfiguration {
    @Bean
    ListUserConversationsFacade listUserConversationsFacade() {
        return new ListUserConversationsFacade(new InMemoryUserConversationRepository(new ConcurrentHashMap<>()));
    }
}
