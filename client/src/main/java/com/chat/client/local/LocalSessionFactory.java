package com.chat.client.local;

import com.chat.client.domain.*;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;
import com.chat.client.utils.ChatsUpdater;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;

public class LocalSessionFactory implements SessionManager.Factory {
    private final ConversationStorageFacade conversationStorageFacade;
    private final RegistrationFacade registrationFacade;
    private final CallbackDispatcher callbackDispatcher;
    private final SessionStorageFacade sessionStorageFacade;
    private final MessageReceiverFacade messageReceiverFacade;

    public LocalSessionFactory(
            ConversationStorageFacade conversationStorageFacade,
            RegistrationFacade registrationFacade,
            CallbackDispatcher callbackDispatcher,
            SessionStorageFacade sessionStorageFacade,
            MessageReceiverFacade messageReceiverFacade
    ) {
        this.conversationStorageFacade = conversationStorageFacade;
        this.registrationFacade = registrationFacade;
        this.callbackDispatcher = callbackDispatcher;
        this.sessionStorageFacade = sessionStorageFacade;
        this.messageReceiverFacade = messageReceiverFacade;
    }

    @Override
    public ChatsService getChatsService(Credentials credentials) {
        return new LocalChatsService(conversationStorageFacade);
    }

    @Override
    public UsersService getUsersService(Credentials credentials) {
        return new LocalUsersService(registrationFacade);
    }

    @Override
    public MessagingClient getMessagingClient(User localUser, Credentials credentials, ChatsService chatsService, ChatsRepository chatsRepository) {
        var messageFactory = new MessageFactory(localUser);
        var chatsUpdater = new ChatsUpdater(chatsService, callbackDispatcher);
        return new LocalMessageClient(localUser, chatsRepository, messageFactory, chatsUpdater, sessionStorageFacade, messageReceiverFacade);
    }
}
