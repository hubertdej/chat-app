package com.chat.server.configuration;

import com.chat.server.sql.SqlFactory;

import com.chat.sql.SqlConversationsEngine;
import com.chat.sql.SqlConversationsLoader;
import com.chat.sql.SqlUsersManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DatabaseConfiguration {
    private static String path = "chat-server.db";
    @Bean
    public SqlConversationsEngine engine() {
        return SqlFactory.getConversationsEngine(path);
    }
    @Bean
    public SqlConversationsLoader loader() {
        return SqlFactory.getConversationsLoader(path);
    }
    @Bean
    public SqlUsersManager manager(){
        return SqlFactory.getUsersManager(path);
    }
}
