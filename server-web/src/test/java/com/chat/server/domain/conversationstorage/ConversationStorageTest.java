package com.chat.server.domain.conversationstorage;

import com.chat.server.configuration.ConversationStorageConfiguration;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.listuserconversations.InMemoryUserConversationRepository;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.sql.SqlEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
//TODO add mocks?
public class ConversationStorageTest {
    ConversationStorageFacade conversationStorageFacade = new ConversationStorageFacade(
            new InMemoryConversationRepository()
    );

    public static void assertListEquals(List<?> first, List<?> second){
        Assertions.assertEquals(first.size(), second.size());
        assertTrue(first.containsAll(second));
        assertTrue(second.containsAll(first));
    }

    @Test
    void addConversation(){
        //given: storage has john and barry conversation
        List<String> members = Arrays.asList("john", "barry");
        UUID conversationId = conversationStorageFacade.add(members);

        //when: user asks for conversation
        Optional<ConversationDto> conversationDto = conversationStorageFacade.get(conversationId);

        //then: module returns john and barry conversation
        Assertions.assertEquals(conversationId, conversationDto.orElseThrow().getConversationId());
        assertListEquals(conversationDto.orElseThrow().getMembers(), members);
    }

    @Test
    void removeConversation(){
        //given: storage has john and barry conversation
        List<String> members = Arrays.asList("john", "barry");
        UUID conversationId = conversationStorageFacade.add(members);

        //when: user asks to remove john and barry's conversation
        conversationStorageFacade.remove(conversationId);

        //then: john and barry's conversation is no longer in module
        assertTrue(conversationStorageFacade.get(conversationId).isEmpty());
    }
}
