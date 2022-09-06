package com.chat.client.domain;

import java.sql.Timestamp;
import java.util.UUID;

public class MessageFactory {
    private final User user;

    public MessageFactory(User user) {
        this.user = user;
    }

    public Message createMessage(UUID chatUUID, String text, String username, Timestamp timestamp) {
        return new Message(chatUUID, text, new User(username), timestamp, username.equals(user.name()));
    }
}
