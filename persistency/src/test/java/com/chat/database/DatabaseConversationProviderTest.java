package com.chat.database;

import com.chat.database.records.DatabaseConversation;
import com.chat.database.records.DatabaseMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
class DatabaseConversationProviderTest {
    @InjectMocks DatabaseConversationProvider provider;
    @Test
    void provideDto() {
        var id = new UUID(12, 34);
        var chatName = "bff";
        var username = "Alice";
        var friend = "Bob";
        var text = "hey";
        var time = 1;
        var members = List.of(username, friend);
        var messages = List.of(new DatabaseMessage(username, id, text, new Timestamp(time)));
        var expectedDatabaseConversation = new DatabaseConversation(id, chatName, members, messages);
        var loader = mock(ConversationsLoader.class);
        doAnswer(invocation -> {
            ConversationReader reader = invocation.getArgument(0);
            reader.readName(chatName);
            members.forEach(reader::readMember);
            messages.forEach(dto -> reader.readMessage(dto.from(), dto.content(), dto.timestamp().getTime()));
            return null;
        }).when(loader).readConversation(any(), any());

        var dto = provider.provideDatabaseConversation(loader, id);

        then(loader).should().readConversation(any(), eq(id));
        Assertions.assertEquals(expectedDatabaseConversation, dto);
    }
}