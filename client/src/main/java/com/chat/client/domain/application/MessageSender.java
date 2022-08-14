package com.chat.client.domain.application;

import java.util.UUID;

public interface MessageSender {
    void sendMessage(UUID chatUUID, String text);
}
