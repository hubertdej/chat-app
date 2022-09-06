package com.chat.client.network;

import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.Credentials;
import com.chat.client.domain.MessageFactory;
import com.chat.client.domain.application.SessionManager;
import com.chat.client.domain.User;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.Dispatcher;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;
import com.chat.client.domain.application.ChatsUpdater;

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
        return new UsersServiceImpl(credentials);
    }

    @Override
    public MessagingClient getMessagingClient(User localUser, Credentials credentials, ChatsService chatsService, ChatsRepository chatsRepository) {
        var chatsUpdater = new ChatsUpdater(chatsService, callbackDispatcher);
        var messageFactory = new MessageFactory(localUser);
        return new WebSocketMessagingClient(localUser, credentials, chatsRepository, dispatcher, chatsUpdater, messageFactory);
    }
}
