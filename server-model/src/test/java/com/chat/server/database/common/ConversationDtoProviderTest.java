package com.chat.server.database.common;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ConversationDtoProviderTest {
    @InjectMocks ConversationDtoProvider provider;
    @Test
    void provideDto() {
        var id = new UUID(12, 34);
        var chatName = "bff";
        var username = "Alice";
        var friend = "Bob";
        var text = "hey";
        var time = 1;
        var members = List.of(username, friend);
        var messages = List.of(new MessageDto(username, id, text, new Timestamp(time)));
        var expectedDto = new ConversationDto(id, chatName, members, messages);
        var loader = mock(ConversationsLoader.class);
        doAnswer(invocation -> {
            ConversationReader reader = invocation.getArgument(0);
            reader.readName(chatName);
            members.forEach(reader::readMember);
            messages.forEach(dto -> reader.readMessage(dto.from(), dto.content(), dto.timestamp().getTime()));
            return null;
        }).when(loader).readConversation(any(), any());

        var dto = provider.provideDto(loader, id);

        then(loader).should().readConversation(any(), eq(id));
        assertEquals(expectedDto, dto);
    }
}