package com.chat.client.domain.application;

public interface MessagingClient extends MessageSender {
    void initialize();
    void close();
}
