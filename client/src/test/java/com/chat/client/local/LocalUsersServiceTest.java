package com.chat.client.local;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.User;
import com.chat.client.domain.application.UsersService;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.registration.RegistrationFacade;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class LocalUsersServiceTest {
    @Test
    void testGetUsersAsync() throws ExecutionException, InterruptedException {
        var lister = mock(RegistrationFacade.class);
        var localUser = new User("Alice");
        var service = new LocalUsersService(lister, localUser);
        var username = "Alice";
        var friend = "Bob";
        var friend2 = "Charlie";
        var friends = List.of(new User(friend), new User(friend2));
        given(lister.listUsers()).willReturn(List.of(username, friend, friend2));

        var list = service.getUsersAsync().get();
        assertEquals(friends, list);
    }
}