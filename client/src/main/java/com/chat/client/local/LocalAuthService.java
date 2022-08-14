package com.chat.client.local;

import com.chat.client.domain.Credentials;
import com.chat.client.domain.application.AuthService;
import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.registration.dto.UserDto;

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
        return CompletableFuture.supplyAsync(() -> {
            registrar.register(new UserDto(username, password));
            return null;
        });
    }

    @Override
    public CompletableFuture<Credentials> loginUserAsync(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            if (!authenticator.authenticate(username, password)) {
                throw new AuthFailedException();
            }
            return new Credentials(username, password);
        });
    }
}
