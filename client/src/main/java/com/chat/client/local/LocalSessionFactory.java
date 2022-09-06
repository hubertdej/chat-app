package com.chat.client.local;

import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.Credentials;
import com.chat.client.domain.MessageFactory;
import com.chat.client.domain.User;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.ChatsUpdater;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.SessionManager;
import com.chat.client.domain.application.UsersService;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;

public class LocalSessionFactory implements SessionManager.Factory {
    private final ConversationStorageFacade conversationStorageFacade;
    private final RegistrationFacade registrationFacade;
    private final CallbackDispatcher callbackDispatcher;
    private final SessionStorageFacade sessionStorageFacade;
    private final MessageReceiverFacade messageReceiverFacade;
    private final ListUserConversationsFacade listUserConversationsFacade;

    public LocalSessionFactory(
            ConversationStorageFacade conversationStorageFacade,
            RegistrationFacade registrationFacade,
            CallbackDispatcher callbackDispatcher,
            SessionStorageFacade sessionStorageFacade,
            MessageReceiverFacade messageReceiverFacade,
            ListUserConversationsFacade listUserConversationsFacade
    ) {
        this.conversationStorageFacade = conversationStorageFacade;
        this.registrationFacade = registrationFacade;
        this.callbackDispatcher = callbackDispatcher;
        this.sessionStorageFacade = sessionStorageFacade;
        this.messageReceiverFacade = messageReceiverFacade;
        this.listUserConversationsFacade = listUserConversationsFacade;
    }

    @Override
    public ChatsService getChatsService(Credentials credentials) {
        return new LocalChatsService(conversationStorageFacade, new User(credentials.username()));
    }

    @Override
    public UsersService getUsersService(Credentials credentials) {
        return new LocalUsersService(registrationFacade, new User(credentials.username()));
    }

    @Override
    public MessagingClient getMessagingClient(User localUser, Credentials credentials, ChatsService chatsService, ChatsRepository chatsRepository) {
        var messageFactory = new MessageFactory(localUser);
        var chatsUpdater = new ChatsUpdater(chatsService, callbackDispatcher);
        return new LocalMessageClient(
                localUser,
                chatsRepository,
                messageFactory,
                chatsUpdater,
                sessionStorageFacade,
                messageReceiverFacade,
                listUserConversationsFacade
        );
    }
}
