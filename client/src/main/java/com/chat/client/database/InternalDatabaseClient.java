package com.chat.client.database;

import com.chat.client.domain.*;
import com.chat.client.domain.application.MessagingClient;
import com.chat.database.ConversationsEngine;
import com.chat.database.ConversationsLoader;

import com.chat.database.DatabaseConversationProvider;
import com.chat.database.records.DatabaseMessage;
import com.chat.server.domain.conversationstorage.ConversationsProvider;
import com.chat.server.domain.conversationstorage.dto.MessageDto;

import java.util.UUID;

public class InternalDatabaseClient implements MessagingClient {
    private final MessagingClient external;
    private final DatabaseConversationProvider provider;
    private final ConversationsLoader loader;
    private final ConversationsEngine engine;
    private final ChatsRepository repository;
    private final MessageFactory factory;

    InternalDatabaseClient(
            MessagingClient external,
            DatabaseConversationProvider provider,
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
            var databaseConversation = provider.provideDatabaseConversation(loader, id);
            var addedChat = new Chat(
                    databaseConversation.id(),
                    databaseConversation.name(),
                    databaseConversation.members().stream().map(User::new).toList());
            for (DatabaseMessage message : databaseConversation.messages()) {
                addedChat.addMessage(
                        factory.createMessage(message.id(), message.content(), message.from(), message.timestamp())
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
    private void handleChatUpdate(Message message) {
        engine.addMessage(message.sender().name(), message.chatUUID(), message.text(), message.timestamp().getTime());
    }
}
