package com.chat.server.database;

import com.chat.database.ConversationsLoader;
import com.chat.database.DatabaseConversationProvider;
import com.chat.database.records.DatabaseConversation;
import com.chat.database.records.DatabaseMessage;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FromDatabaseConversationsProviderTest {
    @Mock
    private ConversationsLoader loader;
    @Mock
    private DatabaseConversationProvider dtoProvider;
    @InjectMocks
    private FromDatabaseConversationsProvider conversationsProvider;
    @Test
    void testProvideConversations() throws NoSuchConversationException {
        var id = new UUID(12, 34);
        var id2 = new UUID(12, 35);
        var name1 = "chatName";
        var username = "Alice";
        var friend = "Bob";
        var members = List.of(username, friend);
        var name2 = "bff";
        var facade = mock(ConversationStorageFacade.class);
        var time = 1;
        var text = "hey";
        var message = new DatabaseMessage(username, id2, text, new Timestamp(time));
        var messageDto = new MessageDto(username, id2, text, new Timestamp(time));
        doAnswer(invocation -> {
            ConversationsLoader.IdsReader reader = invocation.getArgument(0);
            reader.readId(id);
            reader.readId(id2);
            return null;
        }).when(loader).readConversationIds(any());
        given(dtoProvider.provideDatabaseConversation(eq(loader), eq(id)))
                .willReturn(new DatabaseConversation(id, name1, List.of(), List.of()));
        given(dtoProvider.provideDatabaseConversation(eq(loader), eq(id2)))
                .willReturn(new DatabaseConversation(id2, name2, members, List.of(message)));

        conversationsProvider.provideConversations(facade);

        then(loader).should().readConversationIds(any());
        then(dtoProvider).should(times(2)).provideDatabaseConversation(eq(loader), any(UUID.class));
        then(facade).should().add(id, name1, List.of());
        then(facade).should().add(id2, name2, List.of(username, friend));
        then(facade).should().add(id2, messageDto);
    }

    @Test
    void testProvideWithNoSuchConversation() throws NoSuchConversationException {
        var id = new UUID(12, 34);
        var username = "Alice";
        var friend = "Bob";
        var members = List.of(username, friend);
        var name = "bff";
        var facade = mock(ConversationStorageFacade.class);
        var time = 1;
        var timestamp = new Timestamp(time);
        var text = "hey";

        var message = new DatabaseMessage(username, id, text, timestamp);
        var messageDto = new MessageDto(username, id, text, timestamp);
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
        given(dtoProvider.provideDatabaseConversation(eq(loader), eq(id)))
                .willReturn(new DatabaseConversation(id, name, members, List.of(message)));

        try {
            conversationsProvider.provideConversations(facade);
        } catch (RuntimeException e) {
            assertThrows(NoSuchConversationException.class, () -> { throw e.getCause(); });
        }
        then(dtoProvider).should(times(1)).provideDatabaseConversation(any(), any());
        then(facade).should().add(id, name, List.of(username, friend));
        then(facade).should().add(id, messageDto);
    }
}