package com.chat.server.database;

import com.chat.server.database.common.ConversationDtoProvider;
import com.chat.server.database.common.ConversationsLoader;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
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
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FromDatabaseConversationsProviderTest {
    @Mock
    private ConversationsLoader loader;
    @Mock
    private ConversationDtoProvider dtoProvider;
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
        var messageDto = new MessageDto(username, id2, text, new Timestamp(time));
        doAnswer(invocation -> {
            ConversationsLoader.IdsReader reader = invocation.getArgument(0);
            reader.readId(id);
            reader.readId(id2);
            return null;
        }).when(loader).readConversationIds(any());
        given(dtoProvider.provideDto(eq(loader), eq(id)))
                .willReturn(new ConversationDto(id, name1, List.of(), List.of()));
        given(dtoProvider.provideDto(eq(loader), eq(id2)))
                .willReturn(new ConversationDto(id2, name2, members, List.of(messageDto)));

        conversationsProvider.provideConversations(facade);

        then(loader).should().readConversationIds(any());
        then(dtoProvider).should(times(2)).provideDto(eq(loader), any(UUID.class));
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
        var text = "hey";
        var messageDto = new MessageDto(username, id, text, new Timestamp(time));
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
        given(dtoProvider.provideDto(eq(loader), eq(id)))
                .willReturn(new ConversationDto(id, name, members, List.of(messageDto)));

        try {
            conversationsProvider.provideConversations(facade);
        } catch (RuntimeException e) {
            assertThrows(NoSuchConversationException.class, () -> { throw e.getCause(); });
        }
        then(dtoProvider).should(times(1)).provideDto(any(), any());
        then(facade).should().add(id, name, List.of(username, friend));
        then(facade).should().add(id, messageDto);
    }
}