package com.chat.server.infrastructure.rest;

import com.chat.server.testconfiguration.TestConversationStorageConfiguration;
import com.chat.server.testconfiguration.TestRegistrationConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@Import({TestConversationStorageConfiguration.class, TestRegistrationConfiguration.class})
public class UserListIntegrationTest extends IntegrationTest{

    ResultActions listUsers() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/list-users")
                .accept(MediaType.APPLICATION_JSON);
        return mockMvc.perform(request);
    }

    @Test
    void getsAllRegisteredUsers() throws Exception {
        //given: john and barry are registered
        registerUser("john", "pwd");
        registerUser("barry", "pwd");

        //when: user asks for user list
        ResultActions resultActions = listUsers();

        //then: list containing john and barry is returned
        resultActions.andExpect(content().json("[\"barry\", \"john\"]"));
    }


}
