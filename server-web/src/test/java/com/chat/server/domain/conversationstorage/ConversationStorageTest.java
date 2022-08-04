//package com.chat.server.domain.conversationstorage;
//
//import com.chat.server.configuration.ConversationStorageConfiguration;
//import com.chat.server.domain.listconversationids.dto.ConversationIdAddedEvent;
//import com.chat.server.domain.conversationstorage.dto.ConversationDto;
//import com.chat.server.domain.listconversationids.dto.ConversationIdRemovedEvent;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.context.ApplicationEventPublisher;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.*;
//
//public class ConversationStorageTest {
//    ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
//    ConversationStorageFacade conversationStorageFacade = new ConversationStorageConfiguration().conversationStorageFacade(applicationEventPublisher);
//
//    public static void assertListEquals(List<?> first, List<?> second){
//        Assertions.assertEquals(first.size(), second.size());
//        assertTrue(first.containsAll(second));
//        assertTrue(second.containsAll(first));
//    }
//
//    @Test
//    void addConversation(){
//        //given: storage has john and barry conversation
//        List<String> members = Arrays.asList("john", "barry");
//        UUID conversationId = conversationStorageFacade.add(members);
//
//        //when: user asks for conversation
//        Optional<ConversationDto> conversationDto = conversationStorageFacade.get(conversationId);
//
//        //then: module returns john and barry conversation
//        Assertions.assertEquals(conversationId, conversationDto.orElseThrow().getConversationId());
//        assertListEquals(conversationDto.orElseThrow().getMembers(), members);
//    }
//
//    @Test
//    void removeConversation(){
//        //given: storage has john and barry conversation
//        List<String> members = Arrays.asList("john", "barry");
//        UUID conversationId = conversationStorageFacade.add(members);
//
//        //when: user asks to remove john and barry's conversation
//        conversationStorageFacade.remove(conversationId);
//
//        //then: john and barry's conversation is no longer in module
//        assertTrue(conversationStorageFacade.get(conversationId).isEmpty());
//    }
//
//    @Test
//    void addingConversationPublishesEvent(){
//        //when: john and barry conversation is added
//        List<String> members = Arrays.asList("john", "barry");
//        UUID conversationId = conversationStorageFacade.add(members);
//
//        //then: ConversationAddedEvent is published
//        ConversationIdAddedEvent event = new ConversationIdAddedEvent(conversationId, "john,barry", members);
//        verify(applicationEventPublisher, times(1)).publishEvent(event);
//    }
//
//    @Test
//    void removingConversationPublishesEvent(){
//        //given: john and barry conversation is in repository
//        List<String> members = Arrays.asList("john", "barry");
//        UUID conversationId = conversationStorageFacade.add(members);
//
//        //when: john and barry conversation is removed
//        conversationStorageFacade.remove(conversationId);
//
//        //then: ConversationRemovedEvent is published
//        ConversationIdRemovedEvent event = new ConversationIdRemovedEvent(conversationId, members);
//        verify(applicationEventPublisher, times(1)).publishEvent(event);
//    }
//}
