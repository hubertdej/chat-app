package com.chat.server.configuration;

import com.chat.database.UsersEngine;
import com.chat.database.UsersLoader;
import com.chat.server.database.*;
import com.chat.server.domain.registration.RegistrationFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public
class RegistrationConfiguration {
    @Bean
    public RegistrationFacade registrationFacade(UsersLoader loader, UsersEngine engine) {
        return new UsersStorageFactory().getRegistrationFacade(
                new FromDatabaseUsersProvider(loader),
                new UsersDatabase(engine)
        );
    }
}
