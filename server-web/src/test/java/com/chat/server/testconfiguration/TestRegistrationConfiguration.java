package com.chat.server.testconfiguration;

import com.chat.server.domain.registration.InMemoryCredentialsRepository;
import com.chat.server.domain.registration.RegistrationFacade;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestRegistrationConfiguration {
    @Bean
    @Primary
    public RegistrationFacade testRegistrationFacade() {
        return new RegistrationFacade(new InMemoryCredentialsRepository());
    }
}
