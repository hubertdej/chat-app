package com.chat.client;

import com.chat.client.database.InternalDatabaseSessionFactory;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.SessionManager;
import com.chat.client.javafxui.Gui;
import com.chat.client.javafxui.GuiDispatcher;
import com.chat.client.javafxui.WindowFactory;
import com.chat.client.local.LocalAuthService;
import com.chat.client.local.LocalSessionFactory;
import com.chat.client.network.AuthServiceImpl;
import com.chat.client.network.SessionFactory;
import com.chat.client.presentation.PresenterFactory;
import com.chat.client.sql.SqlInternalFactory;
import com.chat.client.validators.ChatNameValidator;
import com.chat.client.validators.PasswordValidator;
import com.chat.client.validators.UsernameValidator;
import com.chat.database.DatabaseConversationProvider;
import com.chat.server.database.ConversationsDatabase;
import com.chat.server.database.ConversationsStorageFactory;
import com.chat.server.database.FromDatabaseConversationsProvider;
import com.chat.server.database.FromDatabaseUsersProvider;
import com.chat.server.database.UsersDatabase;
import com.chat.server.database.UsersStorageFactory;
import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.listuserconversations.InMemoryUserConversationRepository;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import com.chat.server.sql.SqlFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

class Program {
    public static void main(String[] args) {
        runClientWithServer();
    }

    private final static String localDatabasePath = "chat-client-with-server.db";
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
                new InMemoryUserConversationRepository(new ConcurrentHashMap<>())
        );
        var conversationStorageFacade = new ConversationsStorageFactory()
                .getConversationStorageFacade(
                        List.of(userConversationsFacade.conversationObserver()),
                        new ConversationsDatabase(conversationsEngine),
                        new FromDatabaseConversationsProvider(databaseConversationProvider, conversationsLoader));
        var sessionStorageFacade = new SessionStorageFacade(conversationStorageFacade);

        var messageReceiverFacade = new MessageReceiverFacade(
                sessionStorageFacade,
                conversationStorageFacade
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
        var usernameValidator = new UsernameValidator();
        var passwordValidator = new PasswordValidator();
        var chatNameValidator = new ChatNameValidator();

        var presenterFactory = new PresenterFactory(
                new WindowFactory(),
                sessionManager,
                callbackDispatcher,
                usernameValidator,
                passwordValidator,
                chatNameValidator
        );

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
            var usernameValidator = new UsernameValidator();
            var passwordValidator = new PasswordValidator();
            var chatNameValidator = new ChatNameValidator();

            var presenterFactory = new PresenterFactory(
                    new WindowFactory(),
                    sessionManager,
                    callbackDispatcher,
                    usernameValidator,
                    passwordValidator,
                    chatNameValidator
            );

            presenterFactory.openAuthView();
            presenterFactory.openAuthView();
        });
    }
}
