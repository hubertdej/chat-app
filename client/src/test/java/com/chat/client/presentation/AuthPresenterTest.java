package com.chat.client.presentation;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.application.Session;
import com.chat.client.domain.application.SessionManager;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.Session;
import com.chat.client.domain.application.SessionManager;
import com.chat.client.validators.ValidationException;
import com.chat.client.validators.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.mockito.BDDMockito.*;

class AuthPresenterTest {
    private AuthView view;
    private AuthPresenter.Factory factory;
    private SessionManager sessionManager;
    private CallbackDispatcher callbackDispatcher = new CallbackDispatcher(Runnable::run);
    private Validator<String> usernameValidator;
    private Validator<String> passwordValidator;

    private AuthPresenter presenter;

    @BeforeEach
    void setup() {
        view = mock(AuthView.class);
        factory = mock(AuthPresenter.Factory.class);
        sessionManager = mock(SessionManager.class);
        callbackDispatcher = spy(new CallbackDispatcher(Runnable::run));
        usernameValidator = mock(Validator.class);
        passwordValidator = mock(Validator.class);
        presenter = new AuthPresenter(view, factory, sessionManager, callbackDispatcher, usernameValidator, passwordValidator);
    }

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
        then(view).should().indicateRegistrationFailed(any());
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
        then(view).should().indicateLoginFailed(any());
    }

    @Test
    void testRegisterWithInvalidUsername() {
        willThrow(ValidationException.class).given(usernameValidator).validate(any());

        presenter.register("username", "password");

        then(view).should().indicateRegistrationFailed(any());
        then(sessionManager).shouldHaveNoInteractions();
    }

    @Test
    void testRegisterWithInvalidPassword() {
        willThrow(ValidationException.class).given(passwordValidator).validate(any());

        presenter.register("username", "password");

        then(view).should().indicateRegistrationFailed(any());
        then(sessionManager).shouldHaveNoInteractions();
    }

    @Test
    void testLoginWithInvalidUsername() {
        willThrow(ValidationException.class).given(usernameValidator).validate(any());

        presenter.login("username", "password");

        then(view).should().indicateLoginFailed(any());
        then(sessionManager).shouldHaveNoInteractions();
    }

    @Test
    void testLoginWithInvalidPassword() {
        willThrow(ValidationException.class).given(passwordValidator).validate(any());

        presenter.login("username", "password");

        then(view).should().indicateLoginFailed(any());
        then(sessionManager).shouldHaveNoInteractions();
    }
}
