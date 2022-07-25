package com.chat.client.domain.application;

import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatMessage;

public interface MessageSender {
    void sendMessage(Chat chat, ChatMessage message);
}
