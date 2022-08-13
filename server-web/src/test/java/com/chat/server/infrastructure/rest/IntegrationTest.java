package com.chat.server.infrastructure.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

@AutoConfigureMockMvc
abstract public class IntegrationTest {
    @Autowired
    protected MockMvc mockMvc;

    public ResultActions registerUser(String username, String password) throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .content(String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password));
        return mockMvc.perform(request);
    }

    public ResultActions authenticateUser(String username, String password) throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .content(String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password));
        return mockMvc.perform(request);
    }
    public ResultActions addConversation(String name, List<String> members) throws Exception {
        String memberList = '[' + String.join(",",members.stream().map(str -> "\""+str+"\"").toList()) + ']';
        RequestBuilder request = MockMvcRequestBuilders.post("/add-conversation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .content(String.format("{\"name\":\"%s\",\"members\":%s}", name, memberList));
        return mockMvc.perform(request);
    }

    public ResultActions listConversationPreviews(String username, String password) throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/list-conversation-previews")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .content(String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password));
        return mockMvc.perform(request);
    }

    public ResultActions getConversation(String username, String password, UUID conversationId) throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/get-conversation")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.TEXT_PLAIN)
                .content(String.format("{\"username\":\"%s\",\"password\":\"%s\", \"conversationId\":\"%s\"}", username, password, conversationId));
        return mockMvc.perform(request);
    }
}
