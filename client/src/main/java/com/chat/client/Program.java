package com.chat.client;

import com.chat.client.domain.SessionManager;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.javafxui.Gui;
import com.chat.client.javafxui.GuiDispatcher;
import com.chat.client.javafxui.WindowFactory;
import com.chat.client.local.LocalAuthService;
import com.chat.client.local.LocalSessionFactory;
import com.chat.client.network.AuthServiceImpl;
import com.chat.client.network.SessionFactory;
import com.chat.client.presentation.PresenterFactory;
import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.InMemoryConversationRepository;
import com.chat.server.domain.listuserconversations.InMemoryUserConversationRepository;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.registration.InMemoryCredentialsRepository;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;

class Program {
    public static void main(String[] args) {
        runClientWithServer();
    }

    private static void runClientWithServer() {
        Gui.run(() -> {
            var registrationFacade = new RegistrationFacade(new InMemoryCredentialsRepository(), engine);
            var authenticationFacade = new AuthenticationFacade(registrationFacade);
            var conversationStorageFacade = new ConversationStorageFacade(new InMemoryConversationRepository(), engine);
            var sessionStorageFacade = new SessionStorageFacade(conversationStorageFacade);

            var userConversationsFacade = new ListUserConversationsFacade(
                    new InMemoryUserConversationRepository(),
                    conversationStorageFacade
            );

            var messageReceiverFacade = new MessageReceiverFacade(
                    sessionStorageFacade,
                    conversationStorageFacade,
                    userConversationsFacade
            );

            var dispatcher = new GuiDispatcher();
            var callbackDispatcher = new CallbackDispatcher(dispatcher);

            var sessionFactory = new LocalSessionFactory(
                    conversationStorageFacade,
                    registrationFacade,
                    callbackDispatcher,
                    sessionStorageFacade,
                    messageReceiverFacade,
                    userConversationsFacade
            );

            var authService = new LocalAuthService(authenticationFacade, registrationFacade);
            var sessionManager = new SessionManager(authService, sessionFactory);
            var presenterFactory = new PresenterFactory(new WindowFactory(), sessionManager, callbackDispatcher);

            presenterFactory.openAuthView();
            presenterFactory.openAuthView();
        });
    }

    private static void runClient() {
        Gui.run(() -> {
            var dispatcher = new GuiDispatcher();
            var callbackDispatcher = new CallbackDispatcher(dispatcher);
            var sessionFactory = new SessionFactory(dispatcher, callbackDispatcher);

            var authService = new AuthServiceImpl();
            var sessionManager = new SessionManager(authService, sessionFactory);
            var presenterFactory = new PresenterFactory(new WindowFactory(), sessionManager, callbackDispatcher);

            presenterFactory.openAuthView();
            presenterFactory.openAuthView();
        });
    }
}
