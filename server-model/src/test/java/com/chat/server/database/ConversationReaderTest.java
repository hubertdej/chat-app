package com.chat.server.database;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConversationReaderTest {
    private UUID id;
    private ConversationReader reader;

    @BeforeEach //UUID is final class
    void init() {
        id = new UUID(12, 34);
        reader = new ConversationReader(id);
    }

    @Test
    void testBuildEmptyConversation() {
        var chatName = "chat";
        reader.readName(chatName);
        var dto = reader.build();
        assertEquals(new ConversationDto(id, chatName, List.of(), List.of()), dto);
    }

    @Test
    void testBuildConversation() {
        var username = "Alice";
        var friend = "Bob";
        var chatName = "chat";
        var text = "hey";
        var timestamp = 1;

        var expectedDto= new MessageDto(username, id, text, new Timestamp(timestamp));

        reader.readName(chatName);
        reader.readMember(username);
        reader.readMember(friend);
        reader.readMessage(username, id, text, timestamp);
        var dto = reader.build();
        assertEquals(
                new ConversationDto(id, chatName, List.of(username, friend), List.of(expectedDto)), dto
        );
    }
}