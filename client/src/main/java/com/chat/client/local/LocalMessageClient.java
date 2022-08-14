package com.chat.client.local;

import com.chat.client.domain.ChatsRepository;
import com.chat.client.domain.MessageFactory;
import com.chat.client.domain.User;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.utils.ChatsUpdater;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.messagereceiver.dto.MessageReceivedDto;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;

import java.util.List;
import java.util.UUID;

public class LocalMessageClient implements MessagingClient {
    private final SessionStorageFacade sessionStorage;
    private final MessageReceiverFacade messageReceiver;
    private final User localUser;
    private final MessageFactory messageFactory;
    private final ChatsRepository repository;
    private final ChatsUpdater chatsUpdater;

    public LocalMessageClient(
            User localUser,
            ChatsRepository repository,
            MessageFactory messageFactory,
            ChatsUpdater chatsUpdater,
            SessionStorageFacade sessionStorageFacade,
            MessageReceiverFacade messageReceiverFacade
    ) {
        this.localUser = localUser;
        this.repository = repository;
        this.messageFactory = messageFactory;
        this.chatsUpdater = chatsUpdater;
        this.sessionStorage = sessionStorageFacade;
        this.messageReceiver = messageReceiverFacade;
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
            throw new RuntimeException(); //TODO chnage?
        }
    }

    // TODO: Request for messages during initialization!
    @Override
    public void initialize() {
        sessionStorage.addObserver(localUser.name(), this::handler);

        // TODO: Re-implement once this functionality is properly supported on the server side.
        // try {
        //     messageReceiver.receiveRequest(requester, new ListConversationsRequestDto(account.getUsername(), new HashMap<>()));
        // } catch (Exception e) {
        //     throw new RuntimeException();
        // }
    }

    @Override
    public void close() {
        sessionStorage.removeObserver(localUser.name(), this::handler);
    }

    private void handler(List<MessageDto> dtos) {
        dtos.forEach(dto -> {
            var message = messageFactory.createMessage(dto.content(), dto.from(), dto.timestamp());
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
