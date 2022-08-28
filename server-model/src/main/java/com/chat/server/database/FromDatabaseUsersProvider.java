package com.chat.server.database;

import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.registration.UsersProvider;
import com.chat.server.domain.registration.dto.UserDto;

public class FromDatabaseUsersProvider implements UsersProvider {
    private final UsersLoader loader;

    public FromDatabaseUsersProvider(UsersLoader loader) {
        this.loader = loader;
    }

    @Override
    public void readUsers(RegistrationFacade registrationFacade) {
        loader.readUsers((username, password) ->
                registrationFacade.register(new UserDto(username, password))
        );
    }
}
