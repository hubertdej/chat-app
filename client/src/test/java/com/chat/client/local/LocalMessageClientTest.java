package com.chat.client.local;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.*;
import com.chat.client.utils.ChatsUpdater;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.messagereceiver.MessageReceiverFacade;
import com.chat.server.domain.messagereceiver.dto.MessageReceivedDto;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class LocalMessageClientTest extends BaseTestCase {
    @Mock private SessionStorageFacade sessionStorage;
    @Mock private MessageReceiverFacade messageReceiver;
    @Mock private User localUser;
    @Mock private MessageFactory messageFactory;
    @Mock private ChatsRepository repository;
    @Mock private ChatsUpdater chatsUpdater;
    @Mock private ListUserConversationsFacade listUserConversationsFacade;
    @InjectMocks LocalMessageClient client;

    @Test
    void testSendMessageToExistingConversation() throws NoSuchConversationException {
        var id = new UUID(12, 34);
        var text = "hey";
        var username = "Alice";
        given(localUser.name()).willReturn(username);

        client.sendMessage(id, text);


        then(messageReceiver).should().receiveMessage(new MessageReceivedDto(username, id, text));
    }

    @Test
    void testSendMessageToNonExistingConversation() throws NoSuchConversationException {
        var id = new UUID(12, 34);
        var text = "hey";
        var username = "Alice";
        given(localUser.name()).willReturn(username);
        doAnswer(invocation -> {throw new NoSuchConversationException();})
                .when(messageReceiver)
                .receiveMessage(any(MessageReceivedDto.class));

        try {
            client.sendMessage(id, text);
        } catch (RuntimeException e) {
            Assertions.assertThrows(NoSuchConversationException.class, () -> { throw e.getCause(); });
        }

        then(messageReceiver).should().receiveMessage(new MessageReceivedDto(username, id, text));
    }

    @Test
    void testInitialize() {
        var username = "Alice";
        var friend = "Bob";
        var id = new UUID(12, 34);
        var chatName = "bff";
        var members = List.of(new User(friend), new User(username));
        var chat = new Chat(id, chatName, members);
        var timestamp = new Timestamp(1);
        chat.addMessage(new ChatMessage(id, "hey", localUser, timestamp, true));
        var chats = List.of(chat);
        given(localUser.name()).willReturn(username);
        given(repository.getChats()).willReturn(chats);
        var response = "hi";
        var responseTimestamp = new Timestamp(2);
        var responseList = List.of(new MessageDto(friend, id, response, responseTimestamp));
        given(listUserConversationsFacade.listMessages(any())).willReturn(responseList);
        var processedMessage = new ChatMessage(id, response, new User(friend), responseTimestamp, false);
        var processedList = List.of(processedMessage);
        var testClient = new LocalMessageClient(
                localUser,
                repository,
                messageFactory,
                chatsUpdater,
                new SessionStorageFacade(mock(ConversationStorageFacade.class)),
                messageReceiver,
                listUserConversationsFacade);
        given(messageFactory.createMessage(any(), any(), any(), any())).willReturn(processedMessage);

        testClient.initialize();

        then(messageFactory).should().createMessage(id, response, friend, responseTimestamp);
        then(chatsUpdater).should().handleMessages(id, repository, processedList);
    }
}