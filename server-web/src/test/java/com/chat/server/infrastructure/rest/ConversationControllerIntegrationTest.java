//package com.chat.server.infrastructure.rest;
//
//import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
//import com.chat.server.domain.conversationstorage.dto.ConversationDto;
//import com.chat.server.domain.listconversationids.dto.ConversationPreviewDto;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import java.util.List;
//import java.util.UUID;
//
//import static com.chat.server.domain.conversationstorage.ConversationStorageTest.assertListEquals;
//import static org.junit.jupiter.api.Assertions.*;
//
//@WebMvcTest({ConversationController.class, RegistrationController.class})
//public class ConversationControllerIntegrationTest extends IntegrationTest{
//    @Autowired
//    private ConversationStorageFacade conversationStorageFacade;
//    private final ObjectMapper mapper = new ObjectMapper();
//    private final String john = "john";
//    private final String barry = "barry";
//    private final String password = "pwd";
//    private final List<String> members = List.of(john, barry);
//    private final String name = "convo";
//
//    @Test
//    void addConversationTest() throws Exception {
//        //given: john is registered
//        registerUser(john, password);
//        //given: john has a conversation with barry
//        String uuid = addConversation(name, members).andReturn().getResponse().getContentAsString();
//        UUID conversationID = UUID.fromString(uuid);
//
//        //when: user asks for john and barry's conversation
//        ConversationDto conversationDto = conversationStorageFacade.get(conversationID).orElseThrow();
//
//        //then: module returns john and barry's conversation
//        assertEquals(conversationID, conversationDto.getConversationId());
//        assertListEquals(members, conversationDto.getMembers());
//        assertEquals(name, conversationDto.getName());
//    }
//
//    @Test
//    void previewsAreListedForRegisteredUser() throws Exception {
//        //given: john is registered
//        registerUser(john, password);
//        //given: john has a conversation with barry
//        conversationStorageFacade.add(name, members);
//
//        //when: user asks for previews by post request
//        String response = listConversationPreviews(john, password)
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//        List<ConversationPreviewDto> previews = mapper.readValue(response, new TypeReference<>() {});
//
//        //then: module returns john and barry's conversation preview
//        assertEquals(1, previews.size());
//        ConversationPreviewDto preview = previews.get(0);
//        assertEquals(name, preview.name());
//        assertListEquals(members, preview.members());
//        assertNull(preview.messageDto());
//    }
//}
