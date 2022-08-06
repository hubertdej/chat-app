package com.chat.client.domain;

import java.util.ArrayList;
import java.util.List;

public class Conversation {
    private String name = "Self Conversation";
    private List<Message> messages; // local messages?
    Conversation() {
        messages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
