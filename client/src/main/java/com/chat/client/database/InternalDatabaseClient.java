package com.chat.client.database;

import com.chat.client.domain.*;
import com.chat.client.domain.application.MessagingClient;
import com.chat.server.database.common.ConversationDtoProvider;
import com.chat.server.database.common.ConversationsEngine;
import com.chat.server.database.common.ConversationsLoader;
import com.chat.server.domain.conversationstorage.dto.MessageDto;

import java.util.UUID;

public class InternalDatabaseClient implements MessagingClient {
    private final MessagingClient external;
    private final ConversationDtoProvider provider;
    private final ConversationsLoader loader;
    private final ConversationsEngine engine;
    private final ChatsRepository repository;
    private final MessageFactory factory;

    InternalDatabaseClient(
            MessagingClient external,
            ConversationDtoProvider provider,
            ConversationsLoader loader,
            ConversationsEngine engine,
            ChatsRepository repository,
            MessageFactory factory) {
        this.external = external;
        this.provider = provider;
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
            var dto = provider.provideDto(loader, id);
            var addedChat = new Chat(
                    dto.getConversationId(),
                    dto.getName(),
                    dto.getMembers().stream().map(User::new).toList());
            for (MessageDto messageDto : dto.getMessages()) {
                addedChat.addMessage(
                        factory.createMessage(
                            messageDto.to(),
                            messageDto.content(),
                            messageDto.from(),
                            messageDto.timestamp()
                        ));
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

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null) return false;
        if (!(o instanceof  InternalDatabaseClient client)) return false;

        return  client.external.equals(external) &&
                client.provider.equals(provider) &&
                client.loader.equals(loader) &&
                client.engine.equals(engine) &&
                client.repository.equals(repository) &&
                client.factory.equals(factory);
    }
}
