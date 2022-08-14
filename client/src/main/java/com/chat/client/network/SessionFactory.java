package com.chat.client.network;

import com.chat.client.domain.*;
import com.chat.client.domain.application.*;
import com.chat.client.utils.ChatsUpdater;

public class SessionFactory implements SessionManager.Factory {
    private final Dispatcher dispatcher;
    private final CallbackDispatcher callbackDispatcher;

    public SessionFactory(Dispatcher dispatcher, CallbackDispatcher callbackDispatcher) {
        this.dispatcher = dispatcher;
        this.callbackDispatcher = callbackDispatcher;
    }

    @Override
    public ChatsService getChatsService(Credentials credentials) {
        return new ChatsServiceImpl(credentials);
    }

    @Override
    public UsersService getUsersService(Credentials credentials) {
        return new UsersServiceImpl();
    }

    @Override
    public MessagingClient getMessagingClient(User localUser, Credentials credentials, ChatsService chatsService, ChatsRepository chatsRepository) {
        var chatsUpdater = new ChatsUpdater(chatsService, callbackDispatcher);
        var messageFactory = new MessageFactory(localUser);
        return new WebSocketMessagingClient(localUser, credentials, chatsRepository, dispatcher, chatsUpdater, messageFactory);
    }
}
