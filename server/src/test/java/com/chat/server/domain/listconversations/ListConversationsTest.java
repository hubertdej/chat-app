package com.chat.server.domain.listconversations;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.listconversations.dto.ConversationPreviewDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.chat.server.domain.conversationstorage.ConversationStorageTest.assertListEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListConversationsTest {
    private final String john = "john";
    private final String barry = "barry";
    private final ConversationStorageFacade conversationStorageFacade = mock(ConversationStorageFacade.class);
    private final ListConversationsFacade listConversationsFacade = new ListConversationsConfiguration().listConversationsFacade(conversationStorageFacade);
    private final ApplicationContextRunner runner = new ApplicationContextRunner();

    @Test
    void conversationIsListedAfterAdding(){
        //given: john has no conversations

        //when: user adds a conversation to repository
        UUID conversationId = UUID.randomUUID();
        listConversationsFacade.add(john, conversationId);

        //then: conversation is listed
        assertListEquals(
                listConversationsFacade.listConversations(john),
                List.of(conversationId));
    }

    @Test
    void conversationPreviewsAreListed(){
        //given: john has a conversation in ConversationStorage and ListConversationPreviewStorage
        UUID conversationId = UUID.randomUUID();
        String name = "convo";
        List<String> members = List.of(john, barry);
        MessageDto messageDto = new MessageDto(john, conversationId, "hey barry", new Date());
        ConversationDto conversationDto = new ConversationDto(
                conversationId,
                name,
                members,
                List.of(messageDto));
        when(conversationStorageFacade.get(conversationId)).thenReturn(Optional.of(conversationDto));
        listConversationsFacade.add(john, conversationId);

        //when: user asks for john conversations previews
        List<ConversationPreviewDto> previews = listConversationsFacade.listConversationPreviews(john);

        //then: module returns john&barry's conversation preview
        ConversationPreviewDto expectedPreview =  new ConversationPreviewDto(conversationId, name, members, messageDto);
        assertListEquals(List.of(expectedPreview), previews);
    }
}
