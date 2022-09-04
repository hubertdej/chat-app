package com.chat.database;

import com.chat.database.records.DatabaseConversation;
import com.chat.database.records.DatabaseMessage;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ConversationReader implements ConversationsLoader.ConversationReader {
    private final UUID id;
    private String name;
    private List<String> members = new ArrayList<>();
    private List<DatabaseMessage> messages = new ArrayList<>();

    public ConversationReader(UUID id) {
        this.id = id;
    }
    @Override
    public DatabaseConversation build() {
        return new DatabaseConversation(id, name, members, messages);
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
    public void readMessage(String from, String content, long timestampValue) {
        messages.add(new DatabaseMessage(from, id, content, new Timestamp(timestampValue)));
    }
};