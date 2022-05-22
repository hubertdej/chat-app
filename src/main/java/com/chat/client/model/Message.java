package com.chat.client.model;

import java.sql.Timestamp;

public class Message {
    public Message(String text, Timestamp timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }
    private String text;
    private Timestamp timestamp;

    public String getText() {
        return text;
    }
    public Timestamp getTimestamp() { return timestamp; }
}
