package com.chat.client.local;

import com.chat.client.domain.User;
import com.chat.client.domain.application.UsersService;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.registration.RegistrationFacade;

import java.util.List;
import java.util.concurrent.CompletableFuture;
public class LocalUsersService implements UsersService {
    private final RegistrationFacade lister;
    public LocalUsersService(RegistrationFacade registrationFacade) {
        this.lister = registrationFacade;
    }
    @Override
    public CompletableFuture<List<User>> getUsersAsync() {
        return CompletableFuture.supplyAsync(() -> lister.listUsers().stream().map(User::new).toList());
    }
}
