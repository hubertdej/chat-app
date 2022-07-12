package com.chat.client.presentation;

import com.chat.client.domain.Preview;

public class AuthenticationPresenter {
    public interface Factory {
        void openLoggedView(Preview preview);
    }

    private final AuthenticationView view;
    private final Factory factory;

    public AuthenticationPresenter(AuthenticationView view, Factory factory) {
        this.view = view;
        this.factory = factory;
    }

    public void open() {
        view.open();
    }

    public void login(String username, String password) {
        factory.openLoggedView(new Preview());
        view.close();
    }

    public void register(String username, String password) {
        factory.openLoggedView(new Preview());
        view.close();
    }
}
