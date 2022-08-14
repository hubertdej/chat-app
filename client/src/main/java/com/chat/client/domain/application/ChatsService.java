package com.chat.client.domain.application;

import com.chat.client.domain.Chat;
import com.chat.client.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ChatsService {
    CompletableFuture<Chat> createChatAsync(String name, List<User> recipients);

    CompletableFuture<Chat> getChatDetails(UUID chatUUID);
}
