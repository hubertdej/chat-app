package com.chat.server.domain.listuserconversations;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryUserConversationRepositoryTest {
    private InMemoryUserConversationRepository inMemoryUserConversationRepository;
    private Map<String, Map<UUID, ConversationDto>> storage;
    private final static String JOHN = "john";
    @BeforeEach
    void setup(){
        storage = new HashMap<>();
        inMemoryUserConversationRepository = new InMemoryUserConversationRepository(storage);
    }
    @Test
    void conversationDtoIsSuccessfullyStored(){
        UUID conversationId = UUID.randomUUID();
        ConversationDto conversationDto = new ConversationDto(conversationId,
                "foo",
                List.of(),
                List.of());
        //when: conversation is added
        inMemoryUserConversationRepository.add(JOHN, conversationDto);
        //then: conversation can be retrieved
        assertEquals(conversationId, storage.get(JOHN).get(conversationId).getConversationId());
    }

    @Test
    void messageIsSuccessfullyAdded(){
        UUID conversationId = UUID.randomUUID();
        ConversationDto conversationDto = new ConversationDto(conversationId,
                "foo",
                List.of(),
                List.of());
        //given: conversation is in storage
        inMemoryUserConversationRepository.add(JOHN, conversationDto);

        String messageContent = "foo";
        MessageDto messageDto = new MessageDto(JOHN, conversationId, messageContent, new Timestamp(System.currentTimeMillis()));
        //when: message is added
        inMemoryUserConversationRepository.addMessage(messageDto, JOHN);

        //then: message can be retrieved
        assertEquals(1, storage.get(JOHN).get(conversationId).getMessages().size());
        assertEquals(messageContent, storage.get(JOHN).get(conversationId).getMessages().get(0).content());
    }
    @Test
    void conversationIsSuccessfullyRemoved(){
        UUID conversationId = UUID.randomUUID();
        ConversationDto conversationDto = new ConversationDto(conversationId,
                "foo",
                List.of(),
                List.of());
        //given: conversation is in storage
        inMemoryUserConversationRepository.add(JOHN, conversationDto);

        //when: conversation is removed
        inMemoryUserConversationRepository.remove(JOHN, conversationId);

        //then: conversation is no longer in storage
        assertNull(storage.get(JOHN).get(conversationId));
    }
    @Test
    void getReturnsAllConversations(){
        UUID conversationIdA = UUID.randomUUID();
        UUID conversationIdB = UUID.randomUUID();
        ConversationDto conversationDtoA = new ConversationDto(conversationIdA,
                "A",
                List.of(),
                List.of());
        ConversationDto conversationDtoB = new ConversationDto(conversationIdB,
                "B",
                List.of(),
                List.of());
        //given: conversation is in storage
        inMemoryUserConversationRepository.add(JOHN, conversationDtoA);
        inMemoryUserConversationRepository.add(JOHN, conversationDtoB);

        //when: user calls get
        List<UUID> conversationIds = inMemoryUserConversationRepository.get(JOHN).stream().map(ConversationDto::getConversationId).toList();

        List<UUID> expected = List.of(conversationIdA, conversationIdB);
        //then: conversation is no longer in storage
        assertEquals(2, conversationIds.size());
        assertTrue(conversationIds.containsAll(expected) && expected.containsAll(conversationIds));
    }



}
