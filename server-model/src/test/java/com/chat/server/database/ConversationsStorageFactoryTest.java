package com.chat.server.database;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade.ConversationObserver;
import com.chat.server.domain.conversationstorage.ConversationsProvider;
import com.chat.server.domain.conversationstorage.InMemoryConversationRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class ConversationsStorageFactoryTest {
    private ConversationsStorageFactory factory;

    @Test
    void testGetConversationStorage() {
        var preObservers = List.of(mock(ConversationObserver.class));
        var database = mock(ConversationsDatabase.class);
        var provider = mock(ConversationsProvider.class);
        var factory = new ConversationsStorageFactory();

        var facade = factory.getConversationStorageFacade(preObservers, database, provider);

        then(provider).should().provideConversations(facade);
        assertEquals(new ConversationStorageFacade(new InMemoryConversationRepository()), facade);
    }
}