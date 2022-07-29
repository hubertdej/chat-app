package com.chat.server.domain.listuserconversations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListUserConversationsConfiguration {

    @Bean
    ListUserConversationsFacade listUserConversationsFacade(){
        return new ListUserConversationsFacade(new InMemoryUserConversationRepository());
    }
}
