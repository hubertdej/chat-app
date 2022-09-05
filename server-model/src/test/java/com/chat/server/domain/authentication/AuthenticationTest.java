package com.chat.server.domain.authentication;

import com.chat.server.domain.registration.RegistrationFacade;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationTest {
    private final RegistrationFacade registrationFacade = mock(RegistrationFacade.class);
    private final AuthenticationFacade authenticationFacade = new AuthenticationFacade(registrationFacade);
    private final static String JOHN = "john";
    private final static String JOHN_PASSWORD = "johnpassword";

    @Test
    void authenticationSucceedsForRegisteredUser() {
        //given: john is registered
        when(registrationFacade.getCredentials(JOHN)).thenReturn(Optional.of(JOHN_PASSWORD));

        //when: user tries to authenticate john
        boolean authenticationSuccessful = authenticationFacade.authenticate(JOHN, JOHN_PASSWORD);

        //then: authentication is successful
        assertTrue(authenticationSuccessful);
    }

    @Test
    void authenticationFailsForNotRegisteredUser() {
        //given: no one is registered
        when(registrationFacade.getCredentials(any())).thenReturn(Optional.empty());

        //when: user tries to authenticate john
        boolean authenticationSuccessful = authenticationFacade.authenticate(JOHN, JOHN_PASSWORD);

        //then: authentication fails
        assertFalse(authenticationSuccessful);
    }
}
