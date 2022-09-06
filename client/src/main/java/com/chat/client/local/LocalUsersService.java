package com.chat.client.local;

import com.chat.client.domain.User;
import com.chat.client.domain.application.UsersService;
import com.chat.server.domain.registration.RegistrationFacade;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class LocalUsersService implements UsersService {
    private final RegistrationFacade lister;
    private final User localUser;

    public LocalUsersService(RegistrationFacade registrationFacade, User localUser) {
        this.lister = registrationFacade;
        this.localUser = localUser;
    }
    @Override
    public CompletableFuture<List<User>> getUsersAsync() {
        var users = lister.listUsers().stream().map(User::new).collect(Collectors.toList());
        users.remove(localUser);
        return CompletableFuture.completedFuture(users);
    }
}
