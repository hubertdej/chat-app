package com.chat.server.domain.listuserconversations;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.listuserconversations.dto.ListConversationsRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListUserConversationsTest {

    private ConversationStorageFacade conversationStorageFacade = mock(ConversationStorageFacade.class);
    private ListUserConversationsFacade listUserConversationsFacade;
    private UserConversationRepository userConversationRepository = mock(UserConversationRepository.class);
    private final String john = "john";

    @BeforeEach
    void setup(){
        listUserConversationsFacade = new ListUserConversationsFacade(userConversationRepository, conversationStorageFacade);
    }

    @Test
    void testListConversationsFromTimestamp(){
        //given: john has a conversation with 3 messages
        UUID johnConversationId = UUID.randomUUID();
        MessageDto message1 = new MessageDto(john, johnConversationId, "a", Timestamp.valueOf("2018-09-01 09:01:15"));
        MessageDto message2 = new MessageDto(john, johnConversationId, "b", Timestamp.valueOf("2018-09-01 09:01:16"));
        MessageDto message3 = new MessageDto(john, johnConversationId, "c", Timestamp.valueOf("2018-09-01 09:01:17"));
        List<MessageDto> johnConversationMessages = List.of(message1, message2, message3);
        ConversationDto johnConversation = new ConversationDto(johnConversationId, "johnconversation", List.of(john), johnConversationMessages);
        when(userConversationRepository.get(john)).thenReturn(List.of(johnConversation));

        //when: user asks for johns conversation messages starting from 2018-09-01 09:01:16 (exclusive)
        ListConversationsRequestDto listConversationsRequestDto = new ListConversationsRequestDto(john, Map.of(johnConversationId, Timestamp.valueOf("2018-09-01 09:01:16")));
        List<MessageDto> received = listUserConversationsFacade.listMessages(listConversationsRequestDto);

        //then: only message3 is returned
        Assertions.assertEquals(1, received.size());
        Assertions.assertEquals(message3.content(), received.get(0).content());
    }
}
