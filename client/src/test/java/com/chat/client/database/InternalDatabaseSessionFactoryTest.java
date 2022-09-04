package com.chat.client.database;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.*;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.domain.application.UsersService;
import com.chat.server.database.common.ConversationDtoProvider;
import com.chat.server.database.common.ConversationsEngine;
import com.chat.server.database.common.ConversationsLoader;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class InternalDatabaseSessionFactoryTest extends BaseTestCase {
    @Mock private SessionManager.Factory externalFactory;
    @Mock private InternalDatabaseFactory databaseFactory;
    @Mock private ConversationDtoProvider provider;
    @InjectMocks private InternalDatabaseSessionFactory factory;

    @Test
    void testGetChatsService() {
        var credentials = new Credentials("Alice", "1234");
        var expectedService = mock(ChatsService.class);
        given(factory.getChatsService(eq(credentials))).willReturn(expectedService);

        var service = factory.getChatsService(credentials);

        then(externalFactory).should().getChatsService(eq(credentials));
        assertEquals(expectedService, service);
    }

    @Test
    void testGetUsersService() {
        var credentials = new Credentials("Alice", "1234");
        var expectedService = mock(UsersService.class);
        given(factory.getUsersService(eq(credentials))).willReturn(expectedService);

        var service = factory.getUsersService(credentials);

        then(externalFactory).should().getUsersService(eq(credentials));
        assertEquals(expectedService, service);
    }

    @Test
    void testGetMessagingClient() {
        var username = "Alice";
        var credentials = new Credentials(username, "1234");
        var chatsService = mock(ChatsService.class);
        var chatsRepository = mock(ChatsRepository.class);
        var localUser = new User(username);
        var client = mock(MessagingClient.class);
        var engine = mock(ConversationsEngine.class);
        var loader = mock(ConversationsLoader.class);
        var msgFactory = new MessageFactory(new User(username));
        given(externalFactory.getMessagingClient(any(), any(), any(), any())).willReturn(client);
        given(databaseFactory.getEngine(any())).willReturn(engine);
        given(databaseFactory.getLoader(any())).willReturn(loader);
        var internalDatabaseClient = new InternalDatabaseClient(
                client,
                provider,
                loader,
                engine,
                chatsRepository,
                msgFactory
        );
        var actualClient = factory.getMessagingClient(localUser, credentials, chatsService, chatsRepository);

        then(externalFactory).should().getMessagingClient(localUser, credentials, chatsService, chatsRepository);
        then(databaseFactory).should().getEngine(eq(username));
        then(databaseFactory).should().getLoader(eq(username));
        assertEquals(internalDatabaseClient, actualClient);
    }
}