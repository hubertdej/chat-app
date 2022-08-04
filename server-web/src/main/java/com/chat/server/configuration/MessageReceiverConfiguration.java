package com.chat.server.configuration;


import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageReceiverConfiguration {
    @Bean
    public MessageReceiverFacade messageReceiverFacade(SessionStorageFacade sessionStorageFacade, ConversationStorageFacade conversationStorageFacade, ListUserConversationsFacade listUserConversationsFacade){
        return new MessageReceiverFacade(sessionStorageFacade, conversationStorageFacade, listUserConversationsFacade);
    }
}
