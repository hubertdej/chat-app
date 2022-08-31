package com.chat.server.database;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.registration.UsersProvider;
import com.chat.server.domain.registration.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FromDatabaseUsersProviderTest {
    @Mock
    UsersLoader loader;
    @InjectMocks
    FromDatabaseUsersProvider provider;

    @Test
    void testProvideUsers() {
        var facade = mock(RegistrationFacade.class);
        var username = "Alice";
        var password = "1234";
        var friend = "Bob";
        var friendsPassword = "password";
        var userDto = new UserDto(username, password);
        var friendDto = new UserDto(friend, friendsPassword);
        doAnswer(invocation -> {
            UsersLoader.UsersReader reader = invocation.getArgument(0);
            reader.readUser(username, password);
            reader.readUser(friend, friendsPassword);
            return null;
        }).when(loader).readUsers(any());

        provider.provideUsers(facade);

        then(loader).should().readUsers(any(UsersLoader.UsersReader.class));
        then(facade).should().register(userDto);
        then(facade).should().register(friendDto);
    }
}