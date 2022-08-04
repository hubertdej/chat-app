package com.chat.client.domain;

import com.chat.client.domain.application.*;

public class Account {
    private final User user;
    private final String password;
    private final ChatsRepository repository;
    private final MessagingClient client;

    public Account(User user, String password, ChatsRepository repository, MessagingClient client) {
        this.user = user;
        this.password = password;
        this.repository = repository;
        this.client = client;
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
    public ChatsRepository getRepository() { return repository; }
    public MessagingClient getClient() { return client; }
}
