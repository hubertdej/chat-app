package com.chat.client.database;

import com.chat.client.domain.*;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;

public class InternalDatabaseSessionFactory implements SessionManager.Factory {
    private final SessionManager.Factory externalFactory;
    private final InternalDatabaseFactory databaseFactory;

    public InternalDatabaseSessionFactory(
            SessionManager.Factory sessionFactory,
            InternalDatabaseFactory factory) {
        this.externalFactory = sessionFactory;
        this.databaseFactory = factory;
    }

    @Override
    public ChatsService getChatsService(Credentials credentials) {
        return externalFactory.getChatsService(credentials);
    }

    @Override
    public UsersService getUsersService(Credentials credentials) {
        return externalFactory.getUsersService(credentials);
    }

    @Override
    public MessagingClient getMessagingClient(
            User localUser,
            Credentials credentials,
            ChatsService chatsService,
            ChatsRepository chatsRepository) {
        var messageFactory = new MessageFactory(localUser);
        return new InternalDatabaseClient(
                externalFactory.getMessagingClient(localUser, credentials, chatsService, chatsRepository),
                databaseFactory.getLoader(localUser.name()),
                databaseFactory.getEngine(localUser.name()),
                chatsRepository,
                messageFactory
        );
    }
}
