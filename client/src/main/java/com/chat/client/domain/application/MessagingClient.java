package com.chat.client.domain.application;

import com.chat.client.domain.Account;
import com.chat.client.domain.ChatsRepository;

public interface MessagingClient extends MessageSender {
    void initialize(Account account, ChatsRepository chatsRepository);
    void close();
}
