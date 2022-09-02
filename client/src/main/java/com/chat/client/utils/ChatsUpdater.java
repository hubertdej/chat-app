package com.chat.client.utils;

import com.chat.client.domain.ChatMessage;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;

import java.util.UUID;

// TODO: Move this code somewhere else?
public class ChatsUpdater {
    private final ChatsService chatsService;
    private final CallbackDispatcher callbackDispatcher;

    public ChatsUpdater(ChatsService chatsService, CallbackDispatcher callbackDispatcher) {
        this.chatsService = chatsService;
        this.callbackDispatcher = callbackDispatcher;
    }

    public void handleMessages(UUID chatUUID, ChatsRepository repository, Iterable<ChatMessage> messages) {
        var optional = repository.getByUUID(chatUUID);
        if (optional.isPresent()) {
            messages.forEach(message -> optional.get().addMessage(message));
            return;
        }
        callbackDispatcher.addCallback(
                chatsService.getChatDetails(chatUUID),
                chat -> {
                    repository.addChat(chat);
                    messages.forEach(chat::addMessage);
                },
                throwable -> {
                    throw new RuntimeException(throwable); // TODO: Change?
                }
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null) return false;
        if (!(o instanceof ChatsUpdater updater)) return false;
        return chatsService.equals(updater.chatsService) && callbackDispatcher.equals(updater.callbackDispatcher);
    }
}
