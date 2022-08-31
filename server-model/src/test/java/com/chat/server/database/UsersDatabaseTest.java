package com.chat.server.database;

import com.chat.server.domain.registration.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class UsersDatabaseTest {
    @Mock
    private UsersEngine engine;
    @InjectMocks
    private UsersDatabase database;

    @Test
    void notifyRegistered() {
        var username = "Alice";
        var password = "1234";
        var userDto = new UserDto(username, password);

        database.notifyRegistered(userDto);

        then(engine).should().addUser(username, password);
    }
}