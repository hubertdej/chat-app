package com.chat.client.local;

import com.chat.client.domain.Account;
import com.chat.client.domain.User;
import com.chat.client.domain.application.AuthService;
import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.registration.dto.UserDto;
import com.chat.server.domain.registration.dto.UsernameTakenException;

import java.util.concurrent.CompletableFuture;

public class LocalAuthService implements AuthService {
    private final AuthenticationFacade authenticator;
    private final RegistrationFacade registrar;

    public LocalAuthService(
            AuthenticationFacade authenticationFacade,
            RegistrationFacade registrationFacade
    ) {
        this.authenticator = authenticationFacade;
        this.registrar = registrationFacade;
    }
    @Override
    public CompletableFuture<Void> registerUserAsync(String username, String password) {
        try {
            registrar.register(new UserDto(username, password));
            return CompletableFuture.completedFuture(null);
        } catch (UsernameTakenException e) {
            throw new AuthFailedException();
        }
    }
    @Override
    public CompletableFuture<Account> loginUserAsync(String username, String password) {
        if(authenticator.authenticate(username, password)) {
            var account = new Account(
                    new User(username),
                    password
            );
            return CompletableFuture.completedFuture(account);
        } else {
            throw new AuthFailedException();
        }
    }
}
