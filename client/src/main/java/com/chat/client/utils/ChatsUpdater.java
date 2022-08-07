package com.chat.client.utils;

import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatMessage;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.User;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.domain.application.ChatsService;
import com.chat.client.domain.application.Dispatcher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChatsUpdater { //TODO move this code somewhere else?
    private final ChatsService service;
    private final CallbackDispatcher dispatcher;

    public ChatsUpdater(ChatsService service, CallbackDispatcher dispatcher) {
        this.service = service;
        this.dispatcher = dispatcher;
    }

    public void handleMessage(UUID chatUUID, ChatsRepository repository, ChatMessage message) {
        var optional = repository.getByUUID(chatUUID);
        if (optional.isPresent()) {
            repository.getByUUID(chatUUID).get().addMessage(message);
        } else {//TODO change?
            dispatcher.addCallback(
                    service.getMembersByUUID(chatUUID),
                    list-> {
                        var chat = new Chat(chatUUID, "[new chat]", list.stream().map(User::new).toList());
                        repository.addChat(chat);
                    },
                    throwable -> {throw new RuntimeException(throwable);}//TODO change?
            );
        }
    }
}
//    chatsService.createChatAsync(name, recipients),
//            chat -> {
//            chatsRepository.addChat(chat);
//            view.close();
//            },
//            $ -> view.indicateChatCreationFailed()