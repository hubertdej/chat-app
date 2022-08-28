package com.chat.server.configuration;

import com.chat.server.sql.SqlEngine;
import com.chat.server.sql.SqlEngineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
class EngineConfiguration {
    @Bean
    SqlEngine engine(){
        return SqlEngineFactory.getDatabase("chat-server.db");
    }
}

