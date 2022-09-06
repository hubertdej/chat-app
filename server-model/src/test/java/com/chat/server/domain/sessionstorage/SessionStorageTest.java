package com.chat.server.domain.sessionstorage;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

public class SessionStorageTest {
    private final static String JOHN = "john";
    private final ConversationStorageFacade conversationStorageFacade = mock(ConversationStorageFacade.class);
    private final SessionStorageFacade sessionStorageFacade = new SessionStorageFacade(conversationStorageFacade);

    @Test
    void noExceptionIsThrownIfUserTriesToRemoveObserverFromEmptyCollection(){
        assertDoesNotThrow(() -> sessionStorageFacade.removeObserver(JOHN, mock(SessionStorageFacade.Observer.class)));
    }
}