package com.chat.client.local;

import com.chat.client.domain.Chat;
import com.chat.client.domain.User;
import com.chat.client.domain.application.ChatsService;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LocalChatsService implements ChatsService {
    private final ConversationStorageFacade storageFacade;
    public LocalChatsService(ConversationStorageFacade conversationStorageFacade) {
        this.storageFacade = conversationStorageFacade;
    }
    @Override
    public CompletableFuture<Chat> createChatAsync(String name, List<User> recipients) {
        var uuid = storageFacade.add(name, recipients.stream().map(User::name).toList());
        return CompletableFuture.completedFuture(new Chat(uuid, name, recipients));
    }

    @Override
    public CompletableFuture<Chat> getChatDetails(UUID chatUUID) {
        return CompletableFuture.supplyAsync(() -> {
            var dto = storageFacade.get(chatUUID).orElseThrow();
            return new Chat(dto.getConversationId(), dto.getName(), dto.getMembers().stream().map(User::new).toList());
        });
    }
}
