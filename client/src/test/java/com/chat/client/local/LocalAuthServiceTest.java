package com.chat.client.local;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.Credentials;
import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.registration.RegistrationFacade;
import com.chat.server.domain.registration.dto.UserDto;
import com.chat.server.domain.registration.dto.UsernameTakenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Executable;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;

class LocalAuthServiceTest extends BaseTestCase {
    @Mock private AuthenticationFacade authenticationFacade;
    @Mock private RegistrationFacade registrationFacade;

    @InjectMocks LocalAuthService localAuthService;

    @Test
    void testSuccessfulRegistrationAsync() throws ExecutionException, InterruptedException {
        final var username = "Alice";
        final var password = "1234";
        var expectedDto = new UserDto(username, password);

        localAuthService.registerUserAsync(username, password).get();

        then(registrationFacade).should().register(expectedDto);
    }

    @Test
    void testUnsuccessfulRegistrationAsync() throws InterruptedException {
        final var username = "Alice";
        final var password = "1234";
        doAnswer(invocation -> {throw new UsernameTakenException();})
                .when(registrationFacade)
                .register(any(UserDto.class));
        var expectedDto = new UserDto(username, password);

        try {
            localAuthService.registerUserAsync(username, password).get();
            fail("Exception not thrown");
        } catch (ExecutionException e) {
            Assertions.assertThrows(AuthFailedException.class, () -> { throw e.getCause(); });
        }

        then(registrationFacade).should().register(expectedDto);
    }

    @Test
    void testSuccessfulLoginAsync() throws ExecutionException, InterruptedException {
        var username = "Alice";
        var password = "1234";
        given(authenticationFacade.authenticate(any(), any())).willReturn(true);

        var credentials = localAuthService.loginUserAsync(username, password).get();

        then(authenticationFacade).should().authenticate(username, password);
        assertEquals(new Credentials(username, password), credentials);
    }

    @Test
    void testUnsuccessfulLoginAsync() throws InterruptedException {
        var username = "Alice";
        var password = "1234";
        given(authenticationFacade.authenticate(any(), any())).willReturn(false);

        try {
            localAuthService.loginUserAsync(username, password).get();
        } catch (ExecutionException e) {
            Assertions.assertThrows(AuthFailedException.class, () -> { throw e.getCause(); });
        }

        then(authenticationFacade).should().authenticate(username, password);
    }
}