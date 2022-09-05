package com.chat.server.infrastructure.rest;

import com.chat.server.TestAppRunner;
import com.chat.server.domain.registration.RegistrationFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = TestAppRunner.class)
public class RegistrationControllerIntegrationTest extends IntegrationTest {
    @Autowired
    private RegistrationFacade registrationFacade;

    @Test
    void shouldReturnRegisteredUserCredentials() throws Exception {
        String username = "john";
        String password = "pwd";

        //given: john is registered
        registerUser(username, password);

        //when: user asks for john's credentials
        Optional<String> foundPassword = registrationFacade.getCredentials(username);

        //then: registrationFacade returns john's password
        assertThat(foundPassword.orElseThrow()).isEqualTo(password);
    }

    @Test
    void duplicateUsernameTest() throws Exception {
        //given: john is registered
        registerUser("john","pwd");

        //when: user tries to register another user named john
        ResultActions resultActions = registerUser("john", "pwd2");

        //then: server return http 403 forbidden
        resultActions.andExpect(status().isForbidden());
    }
}
