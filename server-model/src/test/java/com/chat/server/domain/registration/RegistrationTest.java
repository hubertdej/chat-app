package com.chat.server.domain.registration;

import com.chat.server.domain.registration.dto.UserDto;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegistrationTest {
    RegistrationFacade registrationFacade = new RegistrationFacade(new InMemoryCredentialsRepository(), engine); //TODO mock?

    @Test
    void listsAllRegisteredUsers(){
        UserDto john = new UserDto("john", "johnpwd");
        UserDto barry = new UserDto("barry", "barrypwd");

        //given: john and barry are registered
        registrationFacade.register(john);
        registrationFacade.register(barry);

        //when: user asks for users list
        List<String> foundUsers = registrationFacade.listUsers();

        //then: john and barry are returned
        List<String> expectedUSers = Arrays.asList(john.username(), barry.username());
        assertEquals(expectedUSers.size(), foundUsers.size());
        assertTrue(expectedUSers.containsAll(foundUsers));
        assertTrue(foundUsers.containsAll(expectedUSers));
    }
}
