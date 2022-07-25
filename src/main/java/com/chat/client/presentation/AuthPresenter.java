package com.chat.client.presentation;

import com.chat.client.domain.AccountRepository;
import com.chat.client.domain.Preview;

public class AuthPresenter {
    public interface Factory {
        void openLoggedView(Preview preview);
    }

    private final AuthView view;
    private final Factory factory;
    private final AccountRepository accountRepository;
    private final CallbackDispatcher callbackDispatcher;

    public AuthPresenter(AuthView view,
                         Factory factory,
                         AccountRepository accountRepository,
                         CallbackDispatcher callbackDispatcher) {
        this.view = view;
        this.factory = factory;
        this.accountRepository = accountRepository;
        this.callbackDispatcher = callbackDispatcher;
    }

    public void open() {
        view.open();
    }

    public void login(String username, String password) {
        view.lockChanges();
        callbackDispatcher.addCallback(
                accountRepository.loginUserAsync(username, password),
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
                accountRepository.registerUserAsync(username, password),
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
