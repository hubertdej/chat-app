package com.chat.server.infrastructure.websocket;

public class Message {
    String content;

    public Message(){}

    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
