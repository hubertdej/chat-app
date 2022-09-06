package com.chat.server.domain.listuserconversations;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationUpdatedEvent;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class ConversationEventHandlerTest {
    private final static String JOHN = "john";
    private final static String BARRY = "barry";

    private final UserConversationRepository userConversationRepository = mock(UserConversationRepository.class);
    private final ListUserConversationsFacade listUserConversationsFacade = new ListUserConversationsFacade(userConversationRepository);

    @Test
    void newConversationEventHandlerAddsConversationToAllMembers(){
        //given: conversationObserver observes listUserConversationsFacade
        ConversationStorageFacade.ConversationObserver conversationObserver = listUserConversationsFacade
                .conversationObserver();

        UUID conversationId = UUID.randomUUID();
        ConversationUpdatedEvent newConversationEvent = new ConversationUpdatedEvent(
                conversationId,
                "foo",
                List.of(JOHN, BARRY),
                null
        );
        //when: observer is notified of ConversationUpdatedEvent indicating
        // a new conversation between John and Barry
        conversationObserver.notifyUpdate(newConversationEvent);

        //then: John and Barry entries in userConversationRepository have the new conversation added to them
        verify(userConversationRepository, times(1)).add(eq(JOHN),
                argThat(conv -> conv.getConversationId() == conversationId));
        verify(userConversationRepository, times(1)).add(eq(BARRY),
                argThat(conv -> conv.getConversationId() == conversationId));
    }
    @Test
    void newMessageIsPropagatedAmongUsersStorages(){
        //given: conversationObserver observes listUserConversationsFacade
        //     and John and Barry have a conversation
        ConversationStorageFacade.ConversationObserver conversationObserver = listUserConversationsFacade
                .conversationObserver();

        UUID conversationId = UUID.randomUUID();
        String messageContent = "Hi Barry";
        ConversationUpdatedEvent newConversationEvent = new ConversationUpdatedEvent(
                conversationId,
                "foo",
                List.of(JOHN, BARRY),
                new MessageDto(JOHN, conversationId, messageContent, new Timestamp(System.currentTimeMillis()))
        );
        //when: observer is notified of ConversationUpdatedEvent indicating
        // a new conversation between John and Barry
        conversationObserver.notifyUpdate(newConversationEvent);

        //then: John and Barry entries in userConversationRepository have the new conversation added to them
        verify(userConversationRepository, times(1)).addMessage(
                argThat(messageDto -> messageDto.content().equals(messageContent)),
                eq(JOHN));
        verify(userConversationRepository, times(1)).addMessage(
                argThat(messageDto -> messageDto.content().equals(messageContent)),
                eq(BARRY));

    }
}
