package com.chat.server.configuration;

import com.chat.server.database.*;
import com.chat.server.domain.registration.CredentialsRepository;
import com.chat.server.domain.registration.InMemoryCredentialsRepository;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.registration.UsersProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RegistrationConfiguration {
    @Bean
    public RegistrationFacade registrationFacade(UsersLoader loader, UsersEngine engine) {
        return UsersStorageFactory.getRegistrationFacade(
                new FromDatabaseUsersProvider(loader),
                new UsersDatabase(engine)
        );
    }
}
