package com.chat.client;

import com.chat.client.database.InternalDatabaseSessionFactory;
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
import com.chat.client.sql.SqlInternalFactory;
import com.chat.database.DatabaseConversationProvider;
import com.chat.server.database.*;
import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.listuserconversations.InMemoryUserConversationRepository;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import com.chat.server.sql.SqlFactory;

import java.util.List;

class Program {
    public static void main(String[] args) {
        runClientWithServer();
    }

    private static String localDatabasePath = "chat-client-with-server.db";
    private static void runClientWithServer() {
        var conversationsEngine = SqlFactory.getConversationsEngine(localDatabasePath);
        var conversationsLoader = SqlFactory.getConversationsLoader(localDatabasePath);
        var usersEngine = SqlFactory.getUsersEngine(localDatabasePath);
        var usersLoader = SqlFactory.getUsersLoader(localDatabasePath);
        var databaseConversationProvider = new DatabaseConversationProvider();
        var registrationFacade = new UsersStorageFactory().getRegistrationFacade(
                new FromDatabaseUsersProvider(usersLoader),
                new UsersDatabase(usersEngine)
        );
        var authenticationFacade = new AuthenticationFacade(registrationFacade);
        var userConversationsFacade = new ListUserConversationsFacade(
                new InMemoryUserConversationRepository()
        );
        var conversationStorageFacade = new ConversationsStorageFactory()
                .getConversationStorageFacade(
                        List.of(userConversationsFacade.conversationObserver()),
                        new ConversationsDatabase(conversationsEngine),
                        new FromDatabaseConversationsProvider(databaseConversationProvider, conversationsLoader));
        var sessionStorageFacade = new SessionStorageFacade(conversationStorageFacade);

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

        var databaseFactory = new SqlInternalFactory();

        var internalSessionFactory = new InternalDatabaseSessionFactory(sessionFactory, databaseFactory, databaseConversationProvider);

        var authService = new LocalAuthService(authenticationFacade, registrationFacade);
        var sessionManager = new SessionManager(authService, internalSessionFactory);
        var presenterFactory = new PresenterFactory(new WindowFactory(), sessionManager, callbackDispatcher);
        Gui.run(() -> {
            presenterFactory.openAuthView();
            presenterFactory.openAuthView();
        });
    }

    private static void runClient() {
        Gui.run(() -> {
            var dispatcher = new GuiDispatcher();
            var callbackDispatcher = new CallbackDispatcher(dispatcher);
            var sessionFactory = new SessionFactory(dispatcher, callbackDispatcher);
            var internalDatabaseSessionFactory = new InternalDatabaseSessionFactory(
                    sessionFactory,
                    new SqlInternalFactory(),
                    new DatabaseConversationProvider()
            );
            var authService = new AuthServiceImpl();
            var sessionManager = new SessionManager(authService, internalDatabaseSessionFactory);
            var presenterFactory = new PresenterFactory(new WindowFactory(), sessionManager, callbackDispatcher);
            presenterFactory.openAuthView();
            presenterFactory.openAuthView();
        });
    }
}
