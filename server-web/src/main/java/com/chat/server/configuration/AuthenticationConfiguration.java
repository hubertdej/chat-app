package com.chat.server.configuration;

import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.registration.RegistrationFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class AuthenticationConfiguration {
    @Bean
    AuthenticationFacade authenticationFacade(RegistrationFacade registrationFacade){
        return new AuthenticationFacade(registrationFacade);
    }
}
