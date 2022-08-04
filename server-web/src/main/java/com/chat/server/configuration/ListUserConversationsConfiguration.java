package com.chat.server.configuration;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.listuserconversations.InMemoryUserConversationRepository;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListUserConversationsConfiguration {
    @Bean
    ListUserConversationsFacade listUserConversationsFacade(ConversationStorageFacade facade){
        return new ListUserConversationsFacade(
                new InMemoryUserConversationRepository(),
                facade.getConversationRepository()
        );
    }
}
