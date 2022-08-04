package com.chat.server.configuration;

import com.chat.server.domain.registration.CredentialsRepository;
import com.chat.server.domain.registration.InMemoryCredentialsRepository;
import com.chat.server.domain.registration.RegistrationFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public
class RegistrationConfiguration {
    @Bean
    public RegistrationFacade registrationFacade(){
        CredentialsRepository credentialsRepository = new InMemoryCredentialsRepository();
        return new RegistrationFacade(credentialsRepository);
    }
}
