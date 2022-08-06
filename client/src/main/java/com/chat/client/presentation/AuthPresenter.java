package com.chat.client.presentation;

import com.chat.client.domain.Account;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.application.AuthService;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.MessagingClient;

public class AuthPresenter {
    public interface Factory {
        void openChatlistView(Account account);
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
                    factory.openChatlistView(account);
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
