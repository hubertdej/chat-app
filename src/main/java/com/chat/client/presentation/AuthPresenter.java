package com.chat.client.presentation;

import com.chat.client.domain.application.AuthService;
import com.chat.client.domain.Preview;
import com.chat.client.domain.application.CallbackDispatcher;

public class AuthPresenter {
    public interface Factory {
        void openLoggedView(Preview preview);
    }

    private final AuthView view;
    private final Factory factory;
    private final AuthService authService;
    private final CallbackDispatcher callbackDispatcher;

    public AuthPresenter(AuthView view,
                         Factory factory,
                         AuthService authService,
                         CallbackDispatcher callbackDispatcher) {
        this.view = view;
        this.factory = factory;
        this.authService = authService;
        this.callbackDispatcher = callbackDispatcher;
    }

    public void open() {
        view.open();
    }

    public void login(String username, String password) {
        view.lockChanges();
        callbackDispatcher.addCallback(
                authService.loginUserAsync(username, password),
                account -> {
                    factory.openLoggedView(new Preview());
                    view.close();
                },
                $ -> {
                    view.indicateLoginFailed();
                    view.unlockChanges();
                }
        );
    }

    public void register(String username, String password) {
        view.lockChanges();
        callbackDispatcher.addCallback(
                authService.registerUserAsync(username, password),
                $ -> {
                    view.indicateRegistrationSuccessful();
                    view.unlockChanges();
                },
                $ -> {
                    view.indicateRegistrationFailed();
                    view.unlockChanges();
                }
        );
    }
}
