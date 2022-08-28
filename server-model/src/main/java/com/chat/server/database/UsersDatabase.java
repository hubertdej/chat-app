package com.chat.server.database;

import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.registration.dto.UserDto;

public class UsersDatabase implements RegistrationFacade.RegistrationObserver {
    private final UsersEngine engine;

    public UsersDatabase(UsersEngine engine) {
        this.engine = engine;
    }

    @Override
    public void notifyRegistered(UserDto user) {
        engine.addUser(user.username(), user.password());
    }
}
