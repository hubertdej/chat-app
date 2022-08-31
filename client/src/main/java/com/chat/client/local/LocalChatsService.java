package com.chat.client.local;

import com.chat.client.domain.Chat;
import com.chat.client.domain.User;
import com.chat.client.domain.application.ChatsService;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LocalChatsService implements ChatsService {
    private final ConversationStorageFacade storageFacade;
    private final User localUser;

    public LocalChatsService(ConversationStorageFacade conversationStorageFacade, User localUser) {
        this.storageFacade = conversationStorageFacade;
        this.localUser = localUser;
    }
    @Override
    public CompletableFuture<Chat> createChatAsync(String name, List<User> friends) {
        var recipients = new ArrayList<>(friends);
        recipients.add(localUser);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof LocalChatsService service)) return false;
        return storageFacade.equals(service.storageFacade) && localUser.equals(service.localUser);
    }
}
