package com.chat.server.database;

import com.chat.server.domain.registration.InMemoryCredentialsRepository;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.registration.UsersProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UsersStorageFactoryTest {
    @InjectMocks
    private UsersStorageFactory factory;
    @Test
    void testGetRegistrationFacade() {
        var provider = mock(UsersProvider.class);
        var database= mock(UsersDatabase.class);

        var facade = factory.getRegistrationFacade(provider, database);

        then(provider).should().provideUsers(facade);
        assertNotNull(facade);
    }
}