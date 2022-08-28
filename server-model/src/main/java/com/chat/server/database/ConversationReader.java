package com.chat.server.database;

import com.chat.server.database.ConversationsEngine;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConversationReader implements ConversationsEngine.ConversationReader {
    private final UUID id;
    private String name;
    private List<String> members = new ArrayList<>();
    private List<MessageDto> messages = new ArrayList<>();

    public ConversationReader(UUID id) {
        this.id = id;
    }
    @Override
    public ConversationDto build() {
        return new ConversationDto(id, name, members, messages);
    }
    @Override
    public void readName(String name) {
        this.name = name;
    }
    @Override
    public void readMember(String username) {
        members.add(username);
    }
    @Override
    public void readMessage(String from, UUID to, String content, long timestampValue) {
        messages.add(new MessageDto(from, to, content, new Timestamp(timestampValue)));
    }
};