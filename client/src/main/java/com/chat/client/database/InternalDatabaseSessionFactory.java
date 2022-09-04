package com.chat.client.database;

import com.chat.client.domain.*;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;
import com.chat.server.database.common.ConversationDtoProvider;

public class InternalDatabaseSessionFactory implements SessionManager.Factory {
    private final SessionManager.Factory externalFactory;
    private final InternalDatabaseFactory databaseFactory;
    private final ConversationDtoProvider provider;

    public InternalDatabaseSessionFactory(
            SessionManager.Factory sessionFactory,
            InternalDatabaseFactory factory,
            ConversationDtoProvider provider) {
        this.externalFactory = sessionFactory;
        this.databaseFactory = factory;
        this.provider = provider;
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
                provider,
                databaseFactory.getLoader(localUser.name()),
                databaseFactory.getEngine(localUser.name()),
                chatsRepository,
                messageFactory
        );
    }
}
