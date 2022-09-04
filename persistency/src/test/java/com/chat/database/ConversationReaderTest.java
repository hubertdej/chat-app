package com.chat.database;

import com.chat.database.records.DatabaseConversation;
import com.chat.database.records.DatabaseMessage;

import org.junit.jupiter.api.Assertions;
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
        var expectedDto = new DatabaseConversation(id, chatName, List.of(), List.of());

        reader.readName(chatName);
        var dto = reader.build();

        Assertions.assertEquals(expectedDto, dto);
    }

    @Test
    void testBuildConversation() {
        var username = "Alice";
        var friend = "Bob";
        var chatName = "chat";
        var text = "hey";
        var timestamp = 1;

        var dto= new DatabaseMessage(username, id, text, new Timestamp(timestamp));
        var expectedConversationDto = new DatabaseConversation(id, chatName, List.of(username, friend), List.of(dto));

        reader.readName(chatName);
        reader.readMember(username);
        reader.readMember(friend);
        reader.readMessage(username, text, timestamp);
        var builtDto = reader.build();

        Assertions.assertEquals(expectedConversationDto, builtDto);
    }
}