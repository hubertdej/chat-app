package com.chat.client.local;

import com.chat.client.domain.*;
import com.chat.client.domain.application.MessagingClient;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.listconversationids.dto.ListConversationsRequestDto;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.sessionstorage.ConversationsRequester;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

public class LocalMessageClient implements MessagingClient {
    private final SessionStorageFacade sessionStorage;
    private final MessageReceiverFacade messageReceiver;
    private Account account;
    private ChatsRepository repository;

    public LocalMessageClient(
            SessionStorageFacade sessionStorageFacade,
            MessageReceiverFacade messageReceiverFacade) {
        this.sessionStorage = sessionStorageFacade;
        this.messageReceiver = messageReceiverFacade;
    }

    @Override
    public void sendMessage(Chat chat, ChatMessage message) {
        try {
            messageReceiver.receiveMessage(
                    new MessageDto(
                            account.getUsername(),
                            chat.getUUID(),
                            message.text(),
                            new Timestamp(System.currentTimeMillis())
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(); //TODO chnage?
        }
    }

    @Override
    public void initialize(Account account, ChatsRepository chatsRepository) {
        this.account = account;
        this.repository = chatsRepository;
        sessionStorage.addObserver(account.getUsername(), this::handleMessage);

        try {
            messageReceiver.receiveRequest(requester, new ListConversationsRequestDto(account.getUsername(), new HashMap<>()));
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void close() {
        sessionStorage.removeObserver(account.getUsername(), this::handleMessage);
    }

    private void handleMessage(MessageDto dto) {
        repository.getByUUID(dto.getTo()).orElseGet(() -> {
            // TODO: Make a request for chat details.
            var chat = new Chat(dto.getTo(), "[new chat]", List.of());
            repository.addChat(chat);
            return chat;
        }).addMessage(new ChatMessage(dto.getContent(), new User(dto.getFrom())));
    }

    private ConversationsRequester requester = response -> { //TODO inject?
            var chats = response.conversations()
                    .stream().map(dto -> new Chat(
                            dto.getConversationId(),
                            dto.getName(),
                            dto.getMembers().stream().map(User::new).toList())
                    ).toList();
            for (Chat chat : chats) repository.addChat(chat);
    };
}
