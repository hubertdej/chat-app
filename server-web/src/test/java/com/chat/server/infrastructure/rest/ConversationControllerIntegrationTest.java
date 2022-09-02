package com.chat.server.infrastructure.rest;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.testconfiguration.TestConversationStorageConfiguration;
import com.chat.server.testconfiguration.TestRegistrationConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static com.chat.server.domain.conversationstorage.ConversationStorageTest.assertListEquals;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import({TestConversationStorageConfiguration.class, TestRegistrationConfiguration.class})
public class ConversationControllerIntegrationTest extends IntegrationTest{
    @Autowired
    private ConversationStorageFacade conversationStorageFacade;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String john = "john";
    private final String barry = "barry";
    private final String password = "pwd";
    private final List<String> members = List.of(john, barry);
    private final String name = "convo";

    @Test
    void addConversationTest() throws Exception {
        //given: john is registered
        registerUser(john, password);
        //given: john has a conversation with barry
        String uuid = addConversation(name, members).andReturn().getResponse().getContentAsString();
        UUID conversationID = UUID.fromString(uuid);

        //when: user asks for john and barry's conversation
        ConversationDto conversationDto = conversationStorageFacade.get(conversationID).orElseThrow();

        //then: module returns john and barry's conversation
        assertEquals(conversationID, conversationDto.getConversationId());
        assertListEquals(members, conversationDto.getMembers());
        assertEquals(name, conversationDto.getName());
    }

    @Test
    void getConversationTest() throws Exception {
        //given: john is registered
        registerUser(john, password);
        //given: john has a conversation with barry
        String uuid = addConversation(name, members).andReturn().getResponse().getContentAsString();
        UUID conversationID = UUID.fromString(uuid);

        //when: user asks for john and barry's conversation
        String response = getConversation(john, password, conversationID)
                .andReturn()
                .getResponse()
                .getContentAsString();
        ConversationDto conversationDto = mapper.readValue(response, ConversationDto.class);

        //then: module returns john and barry's conversation
        assertEquals(conversationID, conversationDto.getConversationId());
        assertListEquals(members, conversationDto.getMembers());
        assertEquals(name, conversationDto.getName());
    }
}
