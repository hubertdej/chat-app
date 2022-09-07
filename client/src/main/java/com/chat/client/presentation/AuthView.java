package com.chat.client.presentation;

public interface AuthView {
    void initialize(AuthPresenter presenter);
    void open();
    void close();
    void lockChanges();
    void unlockChanges();
    void indicateLoginFailed(String message);
    void indicateRegistrationSuccessful();
    void indicateRegistrationFailed(String message);
}
