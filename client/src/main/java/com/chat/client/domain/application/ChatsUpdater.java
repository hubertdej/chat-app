package com.chat.client.domain.application;

import com.chat.client.domain.Message;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;

import java.util.UUID;

public class ChatsUpdater {
    private final ChatsService chatsService;
    private final CallbackDispatcher callbackDispatcher;

    public ChatsUpdater(ChatsService chatsService, CallbackDispatcher callbackDispatcher) {
        this.chatsService = chatsService;
        this.callbackDispatcher = callbackDispatcher;
    }

    public void handleMessages(UUID chatUUID, ChatsRepository repository, Iterable<Message> messages) {
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
}
