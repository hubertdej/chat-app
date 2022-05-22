package com.chat.client.logic;

public interface AuthenticationView {
    void initialize(AuthenticationPresenter presenter);
    void open();
    void close();
    void indicateLoginFailed();
    void indicateRegistrationFailed();
}
