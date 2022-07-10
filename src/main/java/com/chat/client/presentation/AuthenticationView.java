package com.chat.client.presentation;

public interface AuthenticationView {
    void initialize(AuthenticationPresenter presenter);
    void open();
    void close();
    void indicateLoginFailed();
    void indicateRegistrationFailed();
}
