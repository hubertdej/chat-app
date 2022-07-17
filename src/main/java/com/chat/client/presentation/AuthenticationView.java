package com.chat.client.presentation;

public interface AuthenticationView {
    void initialize(AuthenticationPresenter presenter);
    void open();
    void close();
    void lockChanges();
    void unlockChanges();
    void indicateLoginFailed();
    void indicateRegistrationSuccessful();
    void indicateRegistrationFailed();
}
