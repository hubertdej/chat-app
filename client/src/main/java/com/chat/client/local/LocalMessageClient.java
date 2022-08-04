package com.chat.client.local;

import com.chat.client.domain.*;
import com.chat.client.domain.application.MessagingClient;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.listconversationids.dto.ListConversationsRequestDto;
import com.chat.server.domain.messagereceiver.ListConversationsResponse;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.sessionstorage.MessagingSessionException;
import com.chat.server.domain.sessionstorage.ServerMessagingSession;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class LocalMessageClient implements MessagingClient {
    private final SessionStorageFacade sessionStorage;
    private final MessageReceiverFacade messageReceiver;

    private ServerMessagingSession session;
    private Account account;

    public LocalMessageClient(SessionStorageFacade sessionStorageFacade, MessageReceiverFacade messageReceiverFacade) {
        this.sessionStorage = sessionStorageFacade;
        this.messageReceiver = messageReceiverFacade;
    }

    @Override
    public void sendMessage(Chat chat, ChatMessage message) {
        try {
            messageReceiver.receiveMessage(
                    session,
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
        this.session = new ServerMessagingSession() { //TODO inject in constructor?
            @Override
            public void sendMessage(MessageDto dto) {
                chatsRepository.getByUUID(dto.getTo()).orElseGet(() -> {
                    // TODO: Make a request for chat details.
                    var chat = new Chat(dto.getTo(), "[chat name]", List.of());
                    chatsRepository.addChat(chat);
                    return chat;
                }).addMessage(new ChatMessage(dto.getContent(), new User(dto.getFrom())));
            }

            //TODO REDUNDANT TO code in LocalAuthService
            @Override
            public void sendMessage(ListConversationsResponse response) {
                var chats = response.conversations()
                        .stream().map(dto -> new Chat(
                                dto.getConversationId(),
                                dto.getName(),
                                dto.getMembers().stream().map(User::new).toList())
                        ).toList();
                for (Chat chat : chats) chatsRepository.addChat(chat);
            }
        };
        sessionStorage.add(account.getUsername(), session);
    }

    @Override
    public void close() {
        sessionStorage.remove(account.getUsername());
    }
}
