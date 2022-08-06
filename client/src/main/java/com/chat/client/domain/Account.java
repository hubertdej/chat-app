package com.chat.client.domain;

import com.chat.client.domain.application.MessagingClient;

public class Account {
    private final User user;
    private final String password;


    public Account(User user, String password) {
        this.user = user;
        this.password = password;
    }

    public String getUsername() {
        return user.name();
    }

    // TODO:
    //  Avoid storing password in plaintext.
    //  Perhaps introduce Credentials class with hashing.
    public String getPassword() {
        return password;
    }

    public User getUser() {
        return user;
    }
}
