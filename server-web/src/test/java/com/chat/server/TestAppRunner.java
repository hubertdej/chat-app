package com.chat.server;

import com.chat.server.configuration.ConversationStorageConfiguration;
import com.chat.server.configuration.DatabaseConfiguration;
import com.chat.server.configuration.RegistrationConfiguration;
import com.chat.server.testconfiguration.TestConversationStorageConfiguration;
import com.chat.server.testconfiguration.TestRegistrationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({TestConversationStorageConfiguration.class, TestRegistrationConfiguration.class})
@ComponentScan(
        basePackages = {
                "com.chat.server.configuration",
                "com.chat.server.infrastructure"},
        excludeFilters = {@ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {DatabaseConfiguration.class, RegistrationConfiguration.class, ConversationStorageConfiguration.class})})
public class TestAppRunner {
    public static void main(String[] args) {
        SpringApplication.run(TestAppRunner.class, args);
    }
}

