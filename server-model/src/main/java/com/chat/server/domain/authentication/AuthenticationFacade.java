package com.chat.server.domain.authentication;

import com.chat.server.domain.registration.RegistrationFacade;

import java.util.Objects;
import java.util.Optional;

public class AuthenticationFacade {
    private final RegistrationFacade registrationFacade;

    public AuthenticationFacade(RegistrationFacade registrationFacade) {
        this.registrationFacade = registrationFacade;
    }

    public boolean authenticate(String username, String password){
        Optional<String> expectedPassword = registrationFacade.getCredentials(username);
        if(expectedPassword.isEmpty())
            return false;
        return Objects.equals(password, expectedPassword.get());
    }
}
