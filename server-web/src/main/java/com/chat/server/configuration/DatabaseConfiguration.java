package com.chat.server.configuration;

import com.chat.database.ConversationsEngine;
import com.chat.database.ConversationsLoader;
import com.chat.database.UsersEngine;
import com.chat.database.UsersLoader;
import com.chat.server.sql.SqlFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DatabaseConfiguration {
    private static String path = "chat-server.db";
    @Bean
    public ConversationsEngine conversationsEngine() {
        return SqlFactory.getConversationsEngine(path);
    }
    @Bean
    public ConversationsLoader conversationsLoader() {
        return SqlFactory.getConversationsLoader(path);
    }
    @Bean
    public UsersEngine usersEngine() { return SqlFactory.getUsersEngine(path); }
    @Bean
    public UsersLoader usersLoader(){
        return SqlFactory.getUsersLoader(path);
    }
}
