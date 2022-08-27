package com.chat.client.local;

import com.chat.client.domain.Chat;
import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.MessageFactory;
import com.chat.client.domain.User;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.utils.ChatsUpdater;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.listuserconversations.dto.ListConversationsRequestDto;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.messagereceiver.dto.MessageReceivedDto;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LocalMessageClient implements MessagingClient {
    private final SessionStorageFacade sessionStorage;
    private final MessageReceiverFacade messageReceiver;
    private final User localUser;
    private final MessageFactory messageFactory;
    private final ChatsRepository repository;
    private final ChatsUpdater chatsUpdater;
    private final ListUserConversationsFacade listUserConversationsFacade;
    public LocalMessageClient(
            User localUser,
            ChatsRepository repository,
            MessageFactory messageFactory,
            ChatsUpdater chatsUpdater,
            SessionStorageFacade sessionStorageFacade,
            MessageReceiverFacade messageReceiverFacade,
            ListUserConversationsFacade listUserConversationsFacade
    ) {
        this.localUser = localUser;
        this.repository = repository;
        this.messageFactory = messageFactory;
        this.chatsUpdater = chatsUpdater;
        this.sessionStorage = sessionStorageFacade;
        this.messageReceiver = messageReceiverFacade;
        this.listUserConversationsFacade = listUserConversationsFacade;
    }

    @Override
    public void sendMessage(UUID chatUUID, String text) {
        try {
            messageReceiver.receiveMessage(
                    new MessageReceivedDto(
                            localUser.name(),
                            chatUUID,
                            text
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
    
    @Override
    public void initialize() {
        sessionStorage.addObserver(localUser.name(), sessionObserver);
        var map = repository.getChats().stream().collect(
                Collectors.toMap(Chat::getUUID, chat -> chat.getLastMessage().timestamp())
        );
        listUserConversationsFacade.listConversations(new ListConversationsRequestDto(localUser.name(), map));
    }

    @Override
    public void close() {
        sessionStorage.removeObserver(localUser.name(), sessionObserver);
    }


    private final SessionStorageFacade.Observer sessionObserver = this::handler;

    private void handler(List<MessageDto> dtos) {
        dtos.forEach(dto -> {
            var message = messageFactory.createMessage(dto.to(), dto.content(), dto.from(), dto.timestamp());
            chatsUpdater.handleMessages(dto.to(), repository, List.of(message));
        });
    }

    // private final ConversationsRequester requester = response -> {
    //     var chats = response.conversations()
    //             .stream().map(dto -> new Chat(
    //                     dto.getConversationId(),
    //                     dto.getName(),
    //                     dto.getMembers().stream().map(User::new).toList())
    //             ).toList();
    //     for (Chat chat : chats) repository.addChat(chat);
    // };
}
