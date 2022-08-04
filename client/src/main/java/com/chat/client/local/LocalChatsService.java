package com.chat.client.local;

import com.chat.client.domain.Chat;
import com.chat.client.domain.User;
import com.chat.client.domain.application.ChatsService;
import com.chat.server.domain.authentication.AuthenticationFacade;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LocalChatsService implements ChatsService {
    private final ConversationStorageFacade creator;

    public LocalChatsService(ConversationStorageFacade conversationStorageFacade) {
        this.creator = conversationStorageFacade;
    }

    @Override
    public CompletableFuture<Chat> createChatAsync(String name, List<User> recipients) {
        return CompletableFuture.supplyAsync(() -> {
            var uuid = creator.add(name, recipients.stream().map(User::name).toList());
            return new Chat(uuid, name, recipients);
        });
    }
}
