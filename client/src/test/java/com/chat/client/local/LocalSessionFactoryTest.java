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

import static org.assertj.core.api.Assertions.assertThat;
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

        var service = factory.getChatsService(credentials);

        assertTrue(service instanceof LocalChatsService);
    }

    @Test
    void testGetUsersService() {
        var username = "Alice";
        var password = "password";
        var credentials = new Credentials(username, password);

        var service = factory.getUsersService(credentials);

        assertTrue(service instanceof LocalUsersService);
    }

    @Test
    void test() {
        var username = "Alice";
        var password = "password";
        var credentials = new Credentials(username, password);
        var user = new User(username);
        var chatsService = mock(ChatsService.class);
        var chatsRepository = mock(ChatsRepository.class);

        var client = factory.getMessagingClient(user, credentials, chatsService, chatsRepository);

        assertTrue(client instanceof LocalMessageClient);
    }
}