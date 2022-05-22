package com.chat.client.logic;

import com.chat.client.model.EmailValidator;
import com.chat.client.model.Preview;

public class AuthenticationPresenter {
    public interface Factory {
        void openLoggedView(Preview preview);
    }

    private final AuthenticationView view;
    private final Factory factory;
    private final EmailValidator emailValidator = new EmailValidator();

    public AuthenticationPresenter(AuthenticationView view, Factory factory) {
        this.view = view;
        this.factory = factory;
    }

    public void open() {
        view.open();
    }

    public void login(String email, String password) {
        if (!emailValidator.isValid(email)) {
            view.indicateLoginFailed();
            return;
        }
        factory.openLoggedView(new Preview());
        view.close();
    }

    public void register(String email, String username, String password) {
        if (!emailValidator.isValid(email)) {
            view.indicateRegistrationFailed();
            return;
        }
        factory.openLoggedView(new Preview());
        view.close();
    }
}
