package com.chat.client.domain;

import java.sql.Timestamp;

public class MessageFactory {
    private final User user;

    public MessageFactory(User user) {
        this.user = user;
    }

    public ChatMessage createMessage(String text, String username, Timestamp timestamp) {
        return new ChatMessage(text, new User(username), timestamp, username.equals(user.name()));
    }
}
