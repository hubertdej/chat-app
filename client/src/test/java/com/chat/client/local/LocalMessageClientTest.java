package com.chat.client.local;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.*;
import com.chat.client.domain.application.ChatsUpdater;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
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

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

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
        var expectedDto = new MessageReceivedDto(username, id, text);

        client.sendMessage(id, text);


        then(messageReceiver).should().receiveMessage(expectedDto);
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
        var expectedDto = new MessageReceivedDto(username, id, text);

        try {
            client.sendMessage(id, text);
        } catch (RuntimeException e) {
            Assertions.assertThrows(NoSuchConversationException.class, () -> { throw e.getCause(); });
        }

        then(messageReceiver).should().receiveMessage(expectedDto);
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
        chat.addMessage(new Message(id, "hey", localUser, timestamp, true));
        var chats = List.of(chat);
        given(localUser.name()).willReturn(username);
        given(repository.getChats()).willReturn(chats);
        var response = "hi";
        var responseTimestamp = new Timestamp(2);
        var responseList = List.of(new MessageDto(friend, id, response, responseTimestamp));
        given(listUserConversationsFacade.listMessages(any())).willReturn(responseList);
        var processedMessage = new Message(id, response, new User(friend), responseTimestamp, false);
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

    @Test
    void testObserver() {
        var username = "Alice";
        var friend = "Bob";
        var id = new UUID(12, 34);
        var chatName = "bff";
        var members = List.of(new User(friend), new User(username));
        var chat = new Chat(id, chatName, members);
        chat.addMessage(new Message(id, username, new User(username), new Timestamp(1), true));
        var timestamp2 = new Timestamp(2);
        var msg2 = "hi";
        var dto = new MessageDto(friend, id, msg2, timestamp2);
        var timestamp3 = new Timestamp(6);
        var msg3 = "how's going...?";
        var dto2 = new MessageDto(friend, id, msg3, timestamp3);
        var chats = List.of(chat);
        given(localUser.name()).willReturn(username);
        given(repository.getChats()).willReturn(chats);
        var expectedMessage = new Message(id, msg2, new User(friend), timestamp2, false);
        var unwantedMessage = new Message(id, msg3, new User(friend), timestamp3, false);
        given(messageFactory.createMessage(id, msg2, friend, timestamp2)).willReturn(expectedMessage);
        given(messageFactory.createMessage(id, msg3, friend, timestamp3)).willReturn(unwantedMessage);
        var conversationStorage = mock(ConversationStorageFacade.class);
        var testSessionStorage = new SessionStorageFacade(conversationStorage);
        var testClient = new LocalMessageClient(
                localUser,
                repository,
                messageFactory,
                chatsUpdater,
                testSessionStorage,
                messageReceiver,
                listUserConversationsFacade
        );
        given(conversationStorage.get(id)).willReturn(
                Optional.of(new ConversationDto(id, chatName, List.of(username, friend), List.of()))
        );

        testClient.initialize();
        testSessionStorage.propagate(dto);
        testClient.close();
        testSessionStorage.propagate(dto2);

        then(messageFactory)
                .should(times(1))
                .createMessage(id, msg2, friend, timestamp2);
        then(chatsUpdater)
                .should(times(1))
                .handleMessages(id, repository, List.of(expectedMessage));

        then(messageFactory)
                .should(times(0))
                .createMessage(id, msg3, friend, timestamp3);
        then(chatsUpdater)
                .should(times(0))
                .handleMessages(id, repository, List.of(unwantedMessage));
    }
}
