package com.chat.server.infrastructure.rest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({AuthenticationController.class, RegistrationController.class})
public class AuthenticationControllerTest extends IntegrationTest {
    private final String username = "john";
    private final String password = "pwd";

    @Test
    void registeredUserSuccessfullyAuthenticated() throws Exception {

        //given: john is registered
        registerUser(username, password);

        //when: user asks to authenticate john
        ResultActions resultActions = authenticateUser(username, password);

        //then: endpoint returns Status Ok
        resultActions.andExpect(status().isOk());
    }

    @Test
    void unregisteredUserCantBeAuthenticated() throws Exception {
        //given: no user is registered

        //when: user asks to authenticate john
        ResultActions resultActions = authenticateUser(username, password);

        //then: endpoint returns Status Ok
        resultActions.andExpect(status().isForbidden());

    }


}
