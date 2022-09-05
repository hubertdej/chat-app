package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.chat.Helper.assertListEquals;
import static org.junit.jupiter.api.Assertions.*;

public class ConversationStorageTest {
    private final ConversationStorageFacade conversationStorageFacade = new ConversationStorageFacade(
            new InMemoryConversationRepository()
    );
    private final static String JOHN = "john";
    private final static String BARRY = "barry";

    @Test
    void addConversation(){
        //given: storage has john and barry conversation
        List<String> members = Arrays.asList(JOHN, BARRY);
        UUID conversationId = conversationStorageFacade.add(members);

        //when: user asks for conversation
        Optional<ConversationDto> conversationDto = conversationStorageFacade.get(conversationId);

        //then: module returns john and barry conversation
        assertEquals(conversationId, conversationDto.orElseThrow().getConversationId());
        assertListEquals(members, conversationDto.orElseThrow().getMembers());
    }
    
    @Test
    void conversationIsAvailableAfterAdding(){
        //given: storage has john and barry conversation
        List<String> members = Arrays.asList(JOHN, BARRY);
        String conversationName = "johnbarry";
        UUID conversationId = conversationStorageFacade.add(conversationName, members);

        //when: user asks for conversation
        Optional<ConversationDto> conversationDto = conversationStorageFacade.get(conversationId);

        //then: module returns john and barry conversation
        assertEquals(conversationId, conversationDto.orElseThrow().getConversationId());
        assertEquals(conversationName, conversationDto.orElseThrow().getName());
        assertListEquals(members, conversationDto.orElseThrow().getMembers());
    }
    
    @Test
    void MessageIsAddedToConversation() throws NoSuchConversationException {
        //given: storage has john and barry conversation
        List<String> members = Arrays.asList(JOHN, BARRY);
        UUID conversationId = conversationStorageFacade.add(members);
        
        //when: message is added to conversation
        String messageContent = " Hi Barry";
        MessageDto messageDto = new MessageDto(JOHN, conversationId, messageContent, new Timestamp(System.currentTimeMillis()));
        conversationStorageFacade.add(conversationId, messageDto);

        //then: message can be retrieved from conversation
        List<MessageDto> messages = conversationStorageFacade.get(conversationId).orElseThrow().getMessages();
        assertEquals(1, messages.size());
        assertEquals(messageContent, messages.get(0).content());
    }

    @Test
    void removeConversation(){
        //given: storage has john and barry conversation
        List<String> members = Arrays.asList(JOHN, BARRY);
        UUID conversationId = conversationStorageFacade.add(members);

        //when: user asks to remove john and barry's conversation
        conversationStorageFacade.remove(conversationId);

        //then: john and barry's conversation is no longer in module
        assertTrue(conversationStorageFacade.get(conversationId).isEmpty());
    }

    @Test
    void addingMessageToNonExistentConversationThrowsNoSuchConversationException(){
        //given: there are no conversations

        //when: user tries to add a message to non-existent conversation
        UUID conversationId = UUID.randomUUID();
        String messageContent = " Hi Barry";
        MessageDto messageDto = new MessageDto(JOHN, conversationId, messageContent, new Timestamp(System.currentTimeMillis()));

        //then: NoSuchConversationException is thrown
        assertThrows(NoSuchConversationException.class, () -> conversationStorageFacade.add(conversationId, messageDto));

    }
}
