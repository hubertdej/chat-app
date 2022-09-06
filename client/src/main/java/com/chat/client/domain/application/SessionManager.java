package com.chat.client.domain.application;

import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.Credentials;
import com.chat.client.domain.User;

import java.util.concurrent.CompletableFuture;

public class SessionManager {
    public interface Factory {
        ChatsService getChatsService(Credentials credentials);
        UsersService getUsersService(Credentials credentials);
        MessagingClient getMessagingClient(User localUser, Credentials credentials, ChatsService chatsService, ChatsRepository chatsRepository);
    }

    private final AuthService authService;
    private final Factory factory;

    public SessionManager(AuthService authService, Factory factory) {
        this.authService = authService;
        this.factory = factory;
    }

    public CompletableFuture<Void> registerUserAsync(String username, String password) {
        return authService.registerUserAsync(username, password);
    }

    public CompletableFuture<Session> createSessionAsync(String username, String password) {
        return authService.loginUserAsync(username, password).thenApply(
                credentials -> {
                    var localUser = new User(username);
                    var chatsService = factory.getChatsService(credentials);
                    var chatsRepository = new ChatsRepository();
                    return new Session(
                            localUser,
                            credentials,
                            chatsService,
                            factory.getUsersService(credentials),
                            chatsRepository,
                            factory.getMessagingClient(localUser, credentials, chatsService, chatsRepository)
                    );
                }
        );
    }
}
