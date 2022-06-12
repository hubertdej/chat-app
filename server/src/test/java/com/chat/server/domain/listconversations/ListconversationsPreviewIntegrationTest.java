package com.chat.server.domain.listconversations;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ListconversationsPreviewIntegrationTest {
    @Autowired
    ListConversationPreviewsFacade listConversationPreviewsFacade;
    @Autowired
    ConversationStorageFacade conversationStorageFacade;
    String john = "john";

    public void assertListEquals(List<?> first, List<?> second){
        Assertions.assertEquals(first.size(), second.size());
        assertTrue(first.containsAll(second));
        assertTrue(second.containsAll(first));
    }

    @Test
    void conversationAddedEventTest(){
        //given: "convo" is added to conversation storage
        UUID conversationId = conversationStorageFacade.add("convo", List.of(john));

        //when: user asks to list john's conversations
        List<UUID> conversationIds = listConversationPreviewsFacade.listConversations(john);

        //then: "convo" id is returned
        assertListEquals(conversationIds, List.of(conversationId));
    }

    @Test
    void conversationRemovedEventTest(){
        //given: "convo" is in conversation storage and in conversation previews storage
        UUID conversationId = conversationStorageFacade.add("convo", List.of(john));

        //when: "convo" is removed from conversation storage
        conversationStorageFacade.remove(conversationId);

        //then: "convo" is removed from conversation preview storage
        assertTrue(listConversationPreviewsFacade.listConversations(john).isEmpty());
    }
}
