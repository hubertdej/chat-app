package com.chat.client.local;

import com.chat.client.domain.*;
import com.chat.client.domain.application.MessagingClient;
import com.chat.client.utils.ChatsUpdater;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.messagereceiver.dto.MessageReceivedDto;
import com.chat.server.domain.sessionstorage.ConversationsRequester;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;

import java.util.List;

public class LocalMessageClient implements MessagingClient {
    private final SessionStorageFacade sessionStorage;
    private final MessageReceiverFacade messageReceiver;
    private Account account;
    private ChatsRepository repository;
    private ChatsUpdater updater;

    public LocalMessageClient(
            SessionStorageFacade sessionStorageFacade,
            MessageReceiverFacade messageReceiverFacade, ChatsUpdater updater) {
        this.sessionStorage = sessionStorageFacade;
        this.messageReceiver = messageReceiverFacade;
        this.updater = updater;
    }

    @Override
    public void sendMessage(Chat chat, String text) {
        try {
            messageReceiver.receiveMessage(
                    new MessageReceivedDto(
                            account.getUsername(),
                            chat.getUUID(),
                            text
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(); //TODO chnage?
        }
    }

    // TODO: Request for messages during initialization!
    @Override
    public void initialize(Account account, ChatsRepository chatsRepository) {
        this.account = account;
        this.repository = chatsRepository;
        sessionStorage.addObserver(account.getUsername(), handler);

        // TODO: Re-implement once this functionality is properly supported on the server side.
        // try {
        //     messageReceiver.receiveRequest(requester, new ListConversationsRequestDto(account.getUsername(), new HashMap<>()));
        // } catch (Exception e) {
        //     throw new RuntimeException();
        // }
    }

    @Override
    public void close() {
        sessionStorage.removeObserver(account.getUsername(), handler);
    }

    private final SessionStorageFacade.Observer handler = dtos ->
            dtos.forEach(dto -> updater.handleMessages(dto.to(), repository, List.of(new ChatMessage(dto.content(), new User(dto.from()), dto.timestamp()))));

    private final ConversationsRequester requester = response -> { //TODO inject?
            var chats = response.conversations()
                    .stream().map(dto -> new Chat(
                            dto.getConversationId(),
                            dto.getName(),
                            dto.getMembers().stream().map(User::new).toList())
                    ).toList();
            for (Chat chat : chats) repository.addChat(chat);
    };
}
