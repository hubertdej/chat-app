package com.chat.server.domain.sessionstorage;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

public class SessionStorageTest {
    private final static String JOHN = "john";
    private final static String BARRY = "barry";
    private final ConversationStorageFacade conversationStorageFacade = mock(ConversationStorageFacade.class);
    private final SessionStorageFacade sessionStorageFacade = new SessionStorageFacade(conversationStorageFacade);

    @Test
    void noExceptionIsThrownIfUserTriesToRemoveObserverFromEmptyCollection(){
        assertDoesNotThrow(() -> sessionStorageFacade.removeObserver(JOHN, mock(SessionStorageFacade.Observer.class)));
    }

    @Test
    void addingObserverResultsInPropagatingHisMessagesToHim(){
        UUID conversationId = UUID.randomUUID();
        ConversationDto johnBarryConversation = new ConversationDto(
                conversationId,
                "foo",
                List.of(JOHN, BARRY),
                List.of());
        SessionStorageFacade.Observer observer = mock(SessionStorageFacade.Observer.class);
        //given: John's observer is added to sessionStorageFacade and John is in a conversation with Barry
        sessionStorageFacade.addObserver(JOHN, observer);
        when(conversationStorageFacade.get(conversationId)).thenReturn(Optional.of(johnBarryConversation));

        MessageDto messageDto = new MessageDto(BARRY, conversationId, "foo", new Timestamp(System.currentTimeMillis()));
        //when: propagate is called with a message directed to john
        sessionStorageFacade.propagate(messageDto);

        //then: notifyNewMessage is called on John's observer
        verify(observer, times(1)).notifyNewMessage(argThat(list -> list.contains(messageDto)));
    }

    @Test
    void removingObserverResultsInNoLongerPropagatingHisMessagesToHim(){
        UUID conversationId = UUID.randomUUID();
        ConversationDto johnBarryConversation = new ConversationDto(
                conversationId,
                "foo",
                List.of(JOHN, BARRY),
                List.of());
        SessionStorageFacade.Observer observer = mock(SessionStorageFacade.Observer.class);
        //given: John's observer is added to sessionStorageFacade and then removed from it
        // and John is in a conversation with Barry
        sessionStorageFacade.addObserver(JOHN, observer);
        sessionStorageFacade.removeObserver(JOHN, observer);
        when(conversationStorageFacade.get(conversationId)).thenReturn(Optional.of(johnBarryConversation));

        MessageDto messageDto = new MessageDto(BARRY, conversationId, "foo", new Timestamp(System.currentTimeMillis()));
        //when: propagate is called with a message directed to john
        sessionStorageFacade.propagate(messageDto);

        //then: notifyNewMessage is not called on John's observer
        verify(observer, times(0)).notifyNewMessage(argThat(list -> list.contains(messageDto)));
    }
}