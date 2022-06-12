package com.chat.server.domain.authentication;

import com.chat.server.domain.registration.RegistrationFacade;
import lombok.AllArgsConstructor;

import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
public class AuthenticationFacade {
    private final RegistrationFacade registrationFacade;

    public boolean authenticate(String username, String password){
        Optional<String> expectedPassword = registrationFacade.getCredentials(username);
        if(expectedPassword.isEmpty())
            return false;
        return Objects.equals(password, expectedPassword.get());
    }
}
