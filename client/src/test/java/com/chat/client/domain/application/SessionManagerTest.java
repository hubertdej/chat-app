package com.chat.client.domain.application;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.Credentials;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

public class SessionManagerTest extends BaseTestCase {
    @Mock private AuthService authService;
    @Mock private SessionManager.Factory factory;

    @InjectMocks private SessionManager sessionManager;

    @Test
    void testRegisterUser() {
        var username = "username";
        var password = "password";

        given(authService.registerUserAsync(username, password)).willReturn(
                CompletableFuture.completedFuture(null)
        );

        sessionManager.registerUserAsync(username, password);

        then(authService).should().registerUserAsync(username, password);
    }

    @Test
    void testCreateSession() throws ExecutionException, InterruptedException {
        var username = "username";
        var password = "password";
        var credentials = new Credentials(username, password);

        given(authService.loginUserAsync(username, password)).willReturn(
                CompletableFuture.completedFuture(credentials)
        );

        var sessionFuture = sessionManager.createSessionAsync(username, password);
        var session = sessionFuture.get();

        then(authService).should().loginUserAsync(username, password);
        assertEquals(credentials, session.credentials());
    }
}
