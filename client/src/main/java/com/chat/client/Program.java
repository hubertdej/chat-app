package com.chat.client;

import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.fake.RestService;
import com.chat.client.fake.WebSocketMessagingClient;
import com.chat.client.javafxui.Gui;
import com.chat.client.javafxui.GuiCallbackDispatcher;
import com.chat.client.javafxui.WindowFactory;
import com.chat.client.local.LocalAuthService;
import com.chat.client.local.LocalChatsService;
import com.chat.client.local.LocalMessageClient;
import com.chat.client.local.LocalUsersService;
import com.chat.client.presentation.PresenterFactory;
import com.chat.client.presentation.ViewFactory;
import com.chat.client.utils.ChatsUpdater;
import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.InMemoryConversationRepository;
import com.chat.server.domain.listuserconversations.InMemoryUserConversationRepository;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.registration.InMemoryCredentialsRepository;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import javafx.application.Platform;

class Program {
    public static void main(String[] args) {
//        runClient();
        runClientWithServer();
    }

    private static void runClientWithServer() {
        Gui.run(() -> {
            ViewFactory viewFactory = new WindowFactory();
            CallbackDispatcher callbackDispatcher = new GuiCallbackDispatcher();
            RegistrationFacade registration = new RegistrationFacade(new InMemoryCredentialsRepository());
            AuthenticationFacade authentication = new AuthenticationFacade(registration);
            ConversationStorageFacade conversationStorage = new ConversationStorageFacade(
                    new InMemoryConversationRepository()
            );
            SessionStorageFacade sessionStorage = new SessionStorageFacade(conversationStorage);
//            ListConversationIdsFacade listConversationIds = new ListConversationIdsFacade(
//                    new InMemoryConversationIdRepository(),
//                    conversationStorage
//            );
            ListUserConversationsFacade userConversations = new ListUserConversationsFacade(
                    new InMemoryUserConversationRepository(),
                    conversationStorage
            );
            MessageReceiverFacade messageReceiver = new MessageReceiverFacade(
                    sessionStorage,
                    conversationStorage,
                    userConversations
            );
            var chatsService = new LocalChatsService(conversationStorage);
            PresenterFactory factory = new PresenterFactory(
                    viewFactory,
                    new LocalAuthService(authentication, registration),
                    new LocalUsersService(registration),
                    chatsService,
                    callbackDispatcher,
                    ChatsRepository::new,
                    () -> new LocalMessageClient(sessionStorage, messageReceiver, new ChatsUpdater(chatsService, callbackDispatcher))
            );
            factory.openAuthView();
            factory.openAuthView();
        });
    }

    private static void runClient() {
        Gui.run(() -> {
            ViewFactory viewFactory = new WindowFactory();
            RestService service = new RestService();
            CallbackDispatcher callbackDispatcher = new GuiCallbackDispatcher();
            PresenterFactory factory = new PresenterFactory(
                    viewFactory,
                    service,
                    service,
                    service,
                    callbackDispatcher,
                    ChatsRepository::new,
                    () -> new WebSocketMessagingClient(Platform::runLater, new ChatsUpdater(service, callbackDispatcher))
            );
            factory.openAuthView();
            factory.openAuthView();
        });
    }
}
