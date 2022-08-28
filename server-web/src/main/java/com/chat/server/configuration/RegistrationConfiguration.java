package com.chat.server.configuration;

import com.chat.server.domain.registration.CredentialsRepository;
import com.chat.server.domain.registration.InMemoryCredentialsRepository;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.registration.UsersEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RegistrationConfiguration {
    @Bean
    public RegistrationFacade registrationFacade(UsersEngine engine){
        CredentialsRepository credentialsRepository = new InMemoryCredentialsRepository();
        return new RegistrationFacade(credentialsRepository, engine);
    }
}
