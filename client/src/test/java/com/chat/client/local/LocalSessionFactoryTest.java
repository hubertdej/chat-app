package com.chat.client.local;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.Credentials;
import com.chat.client.domain.MessageFactory;
import com.chat.client.domain.User;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.utils.ChatsUpdater;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LocalSessionFactoryTest extends BaseTestCase {
    @Mock
    private ConversationStorageFacade conversationStorageFacade;
    @Mock
    private RegistrationFacade registrationFacade;
    @Mock
    private CallbackDispatcher callbackDispatcher;
    @Mock
    private SessionStorageFacade sessionStorageFacade;
    @Mock
    private MessageReceiverFacade messageReceiverFacade;
    @Mock
    private ListUserConversationsFacade listUserConversationsFacade;
    @InjectMocks
    LocalSessionFactory factory;

    @Test
    void testGetChatsService() {
        var username = "Alice";
        var password = "password";
        var credentials = new Credentials(username, password);
        var expectedService = new LocalChatsService(conversationStorageFacade, new User(username));

        var service = factory.getChatsService(credentials);

        assertEquals(expectedService, service);
    }

    @Test
    void testGetUsersService() {
        var username = "Alice";
        var password = "password";
        var credentials = new Credentials(username, password);
        var expectedService = new LocalUsersService(registrationFacade, new User(username));

        var service = factory.getUsersService(credentials);

        assertEquals(expectedService, service);
    }

    @Test
    void test() {
        var username = "Alice";
        var password = "password";
        var credentials = new Credentials(username, password);
        var user = new User(username);
        var chatsService = mock(ChatsService.class);
        var chatsRepository = mock(ChatsRepository.class);
        var expectedClient = new LocalMessageClient(
                user,
                chatsRepository,
                new MessageFactory(user),
                new ChatsUpdater(chatsService, callbackDispatcher),
                sessionStorageFacade,
                messageReceiverFacade,
                listUserConversationsFacade
        );

        var client = factory.getMessagingClient(user, credentials, chatsService, chatsRepository);

        assertEquals(expectedClient, client);
    }
}