package com.chat.server.domain.registration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RegistrationConfiguration {
    @Bean
    public RegistrationFacade registrationFacade(){
        CredentialsRepository credentialsRepository = new InMemoryCredentialsRepository();
        return new RegistrationFacade(credentialsRepository);
    }
}
