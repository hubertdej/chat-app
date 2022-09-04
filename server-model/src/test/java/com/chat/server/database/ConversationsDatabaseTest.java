package com.chat.server.database;

import com.chat.database.ConversationsEngine;
import com.chat.server.domain.conversationstorage.dto.ConversationRemovedEvent;
import com.chat.server.domain.conversationstorage.dto.ConversationUpdatedEvent;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.then;


@ExtendWith(MockitoExtension.class)
class ConversationsDatabaseTest {
    @Mock
    private ConversationsEngine engine;
    @InjectMocks
    private ConversationsDatabase database;

    @Test
    void testNotifyNewConversation() {
        var id = new UUID(12, 34);
        var chatName = "chat";
        var username = "Alice";
        var friend = "Bob";
        var members = List.of(username, friend);
        MessageDto dto = null;
        var event = new ConversationUpdatedEvent(id, chatName, members, dto);

        database.notifyUpdate(event);

        then(engine).should().addConversation(id, chatName);
        then(engine).should().addMembers(id, members);
    }

    @Test
    void testNotifyNewMessage() {
        var id = new UUID(12, 34);
        var chatName = "chat";
        var username = "Alice";
        var friend = "Bob";
        var members = List.of(username, friend);
        var text = "hey";
        var time = 1;
        MessageDto dto = new MessageDto(username, id, text, new Timestamp(time));
        var event = new ConversationUpdatedEvent(id, chatName, members, dto);

        database.notifyUpdate(event);

        then(engine).should().addMessage(username, id, text, time);
    }

    @Test
    void testRemoveConversation() {
        var id = new UUID(12, 34);
        var username = "Alice";
        var friend = "Bob";
        var members = List.of(username, friend);

        var event = new ConversationRemovedEvent(id, members);

        database.notifyRemove(event);

        then(engine).should().removeConversation(id);
    }
}