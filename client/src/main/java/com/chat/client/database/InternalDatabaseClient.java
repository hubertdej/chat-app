package com.chat.client.database;

import com.chat.client.domain.*;
import com.chat.client.domain.application.MessagingClient;
import com.chat.server.database.common.ConversationReader;
import com.chat.server.database.common.ConversationsEngine;
import com.chat.server.database.common.ConversationsLoader;
import com.chat.server.domain.conversationstorage.dto.MessageDto;

import java.util.UUID;

public class InternalDatabaseClient implements MessagingClient {
    private final MessagingClient external;
    private final ConversationsLoader loader;
    private final ConversationsEngine engine;
    private final ChatsRepository repository;
    private final MessageFactory factory;

    InternalDatabaseClient(
            MessagingClient external,
            ConversationsLoader loader,
            ConversationsEngine engine,
            ChatsRepository repository,
            MessageFactory factory) {
        this.external = external;
        this.loader = loader;
        this.engine = engine;
        this.repository = repository;
        this.factory = factory;
    }

    @Override
    public void sendMessage(UUID chatUUID, String text) {
        external.sendMessage(chatUUID, text);
    }

    @Override
    public void initialize() {
        ConversationsLoader.IdsReader idsReader = id -> {
            var reader = new ConversationReader(id);
            loader.readConversation(reader, id);
            var dto = reader.build();
            var addedChat = new Chat(
                    dto.getConversationId(),
                    dto.getName(),
                    dto.getMembers().stream().map(User::new).toList()
            );
            for (MessageDto messageDto : dto.getMessages()) {
                addedChat.addMessage(
                        factory.createMessage(
                            messageDto.to(),
                            messageDto.content(),
                            messageDto.from(),
                            messageDto.timestamp()
                        )
                );
            }
            addedChat.addObserver(chatObserver, false);
            repository.addChat(addedChat);
        };
        loader.readConversationIds(idsReader);
        repository.addObserver(chatsObserver);
        external.initialize();
    }

    @Override
    public void close() {
        for (Chat chat : repository.getChats()) chat.removeObserver(chatObserver);
        repository.removeObserver(chatsObserver);
        external.close();
    }

    private final ChatsRepository.Observer chatsObserver = this::handleNewChat;
    private final Chat.Observer chatObserver = this::handleChatUpdate;

    private void handleNewChat(Chat chat) {
        engine.addConversation(chat.getUUID(), chat.getName());
        engine.addMembers(chat.getUUID(), chat.getMembers().stream().map(User::name).toList());
        chat.addObserver(chatObserver, true);
    }
    private void handleChatUpdate(ChatMessage message) {
        engine.addMessage(message.sender().name(), message.chatUUID(), message.text(), message.timestamp().getTime());
    }
}
