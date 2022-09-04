package com.chat.client.database;

import com.chat.client.BaseTestCase;
import com.chat.client.domain.*;
import com.chat.client.domain.application.MessagingClient;
import com.chat.server.database.common.ConversationDtoProvider;
import com.chat.server.database.common.ConversationsEngine;
import com.chat.server.database.common.ConversationsLoader;
import com.chat.server.database.common.ConversationsLoader.IdsReader;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class InternalDatabaseClientTest extends BaseTestCase {
    @Mock private MessagingClient external;
    @Mock private ConversationDtoProvider provider;
    @Mock private ConversationsLoader loader;
    @Mock private ConversationsEngine engine;
    @Mock private ChatsRepository repository;
    @Mock private MessageFactory factory;

    @InjectMocks private InternalDatabaseClient internalDatabaseClient;

    @Test
    void testSendMessage() {
        var id = new UUID(12, 34);
        var text = "hey";

        internalDatabaseClient.sendMessage(id, text);

        then(external).should().sendMessage(id, text);
    }

    @Test
    void testInitialize() {
        var id = new UUID(12, 34);
        var id2 = new UUID(12, 35);
        var name1 = "chatName";
        var chat1 = new Chat(id, name1, List.of());
        var username = "Alice";
        var friend = "Bob";
        var members = List.of(username, friend);
        var name2 = "bff";
        var chat2 = new Chat(id2, name2, members.stream().map(User::new).toList());
        var timestamp  = new Timestamp(1);
        var text = "hey";
        var messageDto = new MessageDto(username, id2, text, timestamp);
        doAnswer(invocation -> {
            ConversationsLoader.IdsReader reader = invocation.getArgument(0);
            reader.readId(id);
            reader.readId(id2);
            return null;
        }).when(loader).readConversationIds(any());
        given(provider.provideDto(eq(loader), eq(id)))
                .willReturn(new ConversationDto(id, name1, List.of(), List.of()));
        given(provider.provideDto(eq(loader), eq(id2)))
                .willReturn(new ConversationDto(id2, name2, members, List.of(messageDto)));
        doAnswer(invocation -> {
            ConversationsLoader.IdsReader reader = invocation.getArgument(0);
            reader.readId(id);
            reader.readId(id2);
            return null;
        }).when(loader).readConversationIds(any(IdsReader.class));


        internalDatabaseClient.initialize();


        then(loader).should().readConversationIds(any(IdsReader.class));
        then(external).should().initialize();
        then(provider).should(times(1)).provideDto(eq(loader), eq(id));
        then(provider).should(times(1)).provideDto(eq(loader), eq(id2));
        then(factory).should().createMessage(id2, text, username, timestamp);
        then(repository).should().addChat(chat1);
        then(repository).should().addChat(chat2);
    }

    @Test
    void testClose() {
        internalDatabaseClient.close();

        then(external).should().close();
    }

    @Test
    void testObserver() {
        var id1 = new UUID(12, 34);
        var name1 = "bff";
        var username1 = "Alice";
        var friend1 = "Bob";
        var members1 = List.of(username1, friend1);
        var text1 = "hey";
        var time1 = 1;
        var timestamp1 = new Timestamp(time1);
        var msg1 = new ChatMessage(id1, text1, new User(username1), timestamp1, true);
        var chat = new Chat(id1, name1, members1.stream().map(User::new).toList());
        var id2 = new UUID(12, 35);
        var name2 = "bff2";
        var username2 = "Alice2";
        var friend2 = "Bob2";
        var members2 = List.of(username2, friend2);
        var text2 = "hey2";
        var time2 = 2;
        var timestamp2 = new Timestamp(time2);
        var msg2 = new ChatMessage(id2, text2, new User(username2), timestamp2, true);
        var chat2 = new Chat(id2, name2, members2.stream().map(User::new).toList());

        var testRepository = new ChatsRepository();
        var testInternalDatabaseClent = new InternalDatabaseClient(
                external,
                provider,
                loader,
                engine,
                testRepository,
                factory
        );

        testInternalDatabaseClent.initialize();
        testRepository.addChat(chat);
        chat.addMessage(msg1);
        testInternalDatabaseClent.close();
        testRepository.addChat(chat2);
        chat.addMessage(msg2);

        then(engine).should(times(1)).addConversation(eq(id1), eq(name1));
        then(engine).should(times(1)).addMembers(eq(id1), eq(members1));
        then(engine).should(times(1)).addMessage(username1, id1, text1, time1);

        then(engine).should(times(0)).addConversation(eq(id2), eq(name2));
        then(engine).should(times(0)).addMembers(eq(id2), eq(members2));
        then(engine).should(times(0)).addMessage(username2, id2, text2, time2);
    }
}