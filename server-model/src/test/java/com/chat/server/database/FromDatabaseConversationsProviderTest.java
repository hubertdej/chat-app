package com.chat.server.database;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FromDatabaseConversationsProviderTest {
    @Mock
    private ConversationsLoader loader;
    @InjectMocks
    private FromDatabaseConversationsProvider provider;
    @Test
    void testProvideConversations() throws NoSuchConversationException {
        var id = new UUID(12, 34);
        var id2 = new UUID(12, 35);
        var name1 = "chatName";

        var username = "Alice";
        var friend = "Bob";
        var name2 = "bff";
        var facade = mock(ConversationStorageFacade.class);
        var time = 1;
        var text = "hey";
        var expectedDto = new MessageDto(username, id2, text, new Timestamp(time));
        doAnswer(invocation -> {
            ConversationsLoader.IdsReader reader = invocation.getArgument(0);
            reader.readId(id);
            reader.readId(id2);
            return null;
        }).when(loader).readConversationIds(any());
        doAnswer(invocation -> {
            ConversationsLoader.ConversationReader reader = invocation.getArgument(0);
            reader.readName(name1);
            return null;
        }).when(loader).readConversation(any(), eq(id));
        doAnswer(invocation -> {
            ConversationsLoader.ConversationReader reader = invocation.getArgument(0);
            reader.readName(name2);
            reader.readMember(username);
            reader.readMember(friend);
            reader.readMessage(username, id2, text, time);
            return null;
        }).when(loader).readConversation(any(), eq(id2));

        provider.provideConversations(facade);

        then(loader).should().readConversationIds(any(ConversationsLoader.IdsReader.class));
        then(loader).should(times(2)).readConversation(any(), any());
        then(facade).should().add(id, name1, List.of());
        then(facade).should().add(id2, name2, List.of(username, friend));
        then(facade).should().add(id2, expectedDto);
    }

    @Test
    void testProvideWithNoSuchConversation() throws NoSuchConversationException {
        var id = new UUID(12, 34);
        var username = "Alice";
        var friend = "Bob";
        var name = "bff";
        var facade = mock(ConversationStorageFacade.class);
        var time = 1;
        var text = "hey";
        var expectedDto = new MessageDto(username, id, text, new Timestamp(time));
        doAnswer(invocation -> {
            ConversationsLoader.IdsReader reader = invocation.getArgument(0);
            reader.readId(id);
            return null;
        }).when(loader).readConversationIds(any());
        doAnswer(invocation -> null)
                .when(facade)
                .add(any(), any(), any());
        doAnswer(invocation -> {throw new NoSuchConversationException();})
                .when(facade)
                .add(any(), any(MessageDto.class));
        doAnswer(invocation -> {
            ConversationsLoader.ConversationReader reader = invocation.getArgument(0);
            reader.readName(name);
            reader.readMember(username);
            reader.readMember(friend);
            reader.readMessage(username, id, text, time);
            return null;
        }).when(loader).readConversation(any(), any());
        try {
            provider.provideConversations(facade);
        } catch (RuntimeException e) {
            assertThrows(NoSuchConversationException.class, () -> { throw e.getCause(); });
        }

        then(loader).should(times(1)).readConversation(any(), any());
        then(facade).should().add(id, name, List.of(username, friend));
        then(facade).should().add(id, expectedDto);
    }
}