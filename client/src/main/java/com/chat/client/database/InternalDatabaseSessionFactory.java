package com.chat.client.database;

import com.chat.client.domain.*;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;

public class InternalDatabaseSessionFactory implements SessionManager.Factory {
    private final SessionManager.Factory external;
    private final InternalDatabaseFactory factory;

    public InternalDatabaseSessionFactory(SessionManager.Factory external, InternalDatabaseFactory factory) {
        this.external = external;
        this.factory = factory;
    }

    @Override
    public ChatsService getChatsService(Credentials credentials) {
        return external.getChatsService(credentials);
    }

    @Override
    public UsersService getUsersService(Credentials credentials) {
        return external.getUsersService(credentials);
    }

    @Override
    public MessagingClient getMessagingClient(
            User localUser,
            Credentials credentials,
            ChatsService chatsService,
            ChatsRepository chatsRepository) {
        var messageFactory = new MessageFactory(localUser);
        return new InternalDatabaseClient(
                external.getMessagingClient(localUser, credentials, chatsService, chatsRepository),
                factory.getLoader(localUser.name()),
                factory.getEngine(localUser.name()),
                chatsRepository,
                messageFactory
                );
    }
}
