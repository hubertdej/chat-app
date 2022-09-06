package com.chat.server.domain.messagereceiver;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.messagereceiver.dto.MessageReceivedDto;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class MessageReceiverTest {
    private final static String JOHN = "john";
    private final ConversationStorageFacade conversationStorageFacade = mock(ConversationStorageFacade.class);
    private final SessionStorageFacade sessionStorageFacade = mock(SessionStorageFacade.class);
    private final MessageReceiverFacade messageReceiverFacade = new MessageReceiverFacade(sessionStorageFacade, conversationStorageFacade);

    @Test
    void messagesArePropagated() throws NoSuchConversationException {
        UUID conversationId = UUID.randomUUID();
        MessageReceivedDto messageReceivedDto = new MessageReceivedDto(JOHN, conversationId, "foo");
        //when: message is received
        messageReceiverFacade.receiveMessage(messageReceivedDto);
        //then: sessionStorageFacade.propagate is called
        verify(sessionStorageFacade, times(1)).propagate(argThat(messageDto -> messageDto.content().equals("foo")));

    }

    @Test
    void messagesArePersisted() throws NoSuchConversationException {
        UUID conversationId = UUID.randomUUID();
        MessageReceivedDto messageReceivedDto = new MessageReceivedDto(JOHN, conversationId, "foo");
        //when: message is received
        messageReceiverFacade.receiveMessage(messageReceivedDto);
        //then: sessionStorageFacade.propagate is called
        verify(conversationStorageFacade, times(1)).add(eq(conversationId), argThat(messageDto -> messageDto.content().equals("foo")));

    }
}
