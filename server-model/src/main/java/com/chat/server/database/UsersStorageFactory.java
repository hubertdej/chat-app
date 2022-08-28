package com.chat.server.database;

import com.chat.server.domain.registration.InMemoryCredentialsRepository;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.registration.UsersProvider;

public class UsersStorageFactory {
    public static RegistrationFacade getRegistrationFacade(UsersProvider provider, UsersDatabase database) {
        var facade = new RegistrationFacade(new InMemoryCredentialsRepository());
        provider.readUsers(facade);
        facade.addObserver(database);
        return facade;
    }
}
