package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.ConversationRemovedEvent;
import com.chat.server.domain.conversationstorage.dto.ConversationUpdatedEvent;
import org.junit.jupiter.api.Test;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade.ConversationObserver;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConversationObserverTest {
    private final ConversationStorageFacade conversationStorageFacade = new ConversationStorageFacade(
            new InMemoryConversationRepository()
    );
    private final static String JOHN = "john";

    @Test
    void notifyUpdateIsCalledOnConversationAddition(){
        ReplicaObserver fooObserver = new ReplicaObserver();
        //given: fooObserver is added to conversationStorageFacade
        conversationStorageFacade.addObserver(fooObserver);

        String fooConversation = "fooConversation";
        //when: conversation is added
        UUID conversationId = conversationStorageFacade.add(fooConversation, List.of(JOHN));

        ConversationUpdatedEvent conversationUpdatedEvent = fooObserver.getLastConversationUpdatedEvent();
        //then:
        assertEquals(conversationId, conversationUpdatedEvent.conversationId());
    }

    @Test
    void notifyUpdateIsCalledOnConversationRemoval(){
        ReplicaObserver fooObserver = new ReplicaObserver();
        String fooConversation = "fooConversation";
        //given: fooObserver is added to conversationStorageFacade and fooConversation is in Storage
        conversationStorageFacade.addObserver(fooObserver);
        UUID conversationId = conversationStorageFacade.add(fooConversation, List.of(JOHN));

        //when: conversation is added
        conversationStorageFacade.remove(conversationId);

        ConversationRemovedEvent conversationRemovedEvent = fooObserver.getLastConversationRemovedEvent();
        //then:
        assertEquals(conversationId, conversationRemovedEvent.conversationId());
    }

    private static class ReplicaObserver implements ConversationObserver {
        private ConversationUpdatedEvent lastConversationUpdatedEvent;
        private ConversationRemovedEvent lastConversationRemovedEvent;
        @Override
        public void notifyUpdate(ConversationUpdatedEvent event) {
            lastConversationUpdatedEvent = event;
        }

        @Override
        public void notifyRemove(ConversationRemovedEvent event) {
            lastConversationRemovedEvent = event;
        }

        public ConversationUpdatedEvent getLastConversationUpdatedEvent() {
            return lastConversationUpdatedEvent;
        }

        public ConversationRemovedEvent getLastConversationRemovedEvent() {
            return lastConversationRemovedEvent;
        }
    }
}
