package com.chat.server.configuration;

import com.chat.server.database.common.ConversationsEngine;
import com.chat.server.database.common.ConversationsLoader;
import com.chat.server.sql.SqlConversationsEngine;
import com.chat.server.sql.SqlConversationsLoader;
import com.chat.server.sql.SqlFactory;
import com.chat.server.sql.SqlUsersManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ConversationsEngineConfiguration {
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
