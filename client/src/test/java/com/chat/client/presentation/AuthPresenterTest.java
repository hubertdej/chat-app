package com.chat.client.presentation;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.application.Session;
import com.chat.client.domain.application.SessionManager;
import com.chat.client.domain.application.CallbackDispatcher;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.concurrent.CompletableFuture;

import static org.mockito.BDDMockito.*;

class AuthPresenterTest extends BaseTestCase {
    @Mock private AuthView view;
    @Mock private AuthPresenter.Factory factory;
    @Mock private SessionManager sessionManager;
    @Spy private final CallbackDispatcher callbackDispatcher = new CallbackDispatcher(Runnable::run);

    @InjectMocks private AuthPresenter presenter;

    @Test
    void testOpen() {
        presenter.open();

        then(view).should().open();
    }

    @Test
    void testClose() {
        presenter.close();

        then(view).should().close();
    }

    @Test
    void testSuccessfulRegistration() {
        var username = "username";
        var password = "password";
        var future = CompletableFuture.completedFuture((Void) null);
        given(sessionManager.registerUserAsync(any(), any())).willReturn(future);

        presenter.register(username, password);

        then(sessionManager).should().registerUserAsync(username, password);
        then(view).should().indicateRegistrationSuccessful();
    }

    @Test
    void testUnsuccessfulRegistration() {
        var username = "username";
        var password = "password";
        CompletableFuture<Void> future = CompletableFuture.failedFuture(new Exception());
        given(sessionManager.registerUserAsync(any(), any())).willReturn(future);

        presenter.register(username, password);

        then(sessionManager).should().registerUserAsync(username, password);
        then(view).should().indicateRegistrationFailed();
    }

    @Test
    void testSuccessfulLogin() {
        var username = "username";
        var password = "password";
        var session = new Session(null, null, null, null, null, null);
        var future = CompletableFuture.completedFuture(session);
        given(sessionManager.createSessionAsync(any(), any())).willReturn(future);

        presenter.login(username, password);

        then(sessionManager).should().createSessionAsync(username, password);
        then(factory).should().openChatlistView(any(), any(), any(), any(), any());
        then(view).should().close();
    }

    @Test
    void testUnsuccessfulLogin() {
        var username = "username";
        var password = "password";
        CompletableFuture<Session> future = CompletableFuture.failedFuture(new Exception());
        given(sessionManager.createSessionAsync(any(), any())).willReturn(future);

        presenter.login(username, password);

        then(sessionManager).should().createSessionAsync(username, password);
        then(view).should().indicateLoginFailed();
    }
}
