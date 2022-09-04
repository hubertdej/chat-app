package com.chat.database;

import com.chat.database.records.DatabaseConversation;

import java.util.UUID;

public class DatabaseConversationProvider {
    public DatabaseConversation provideDatabaseConversation(ConversationsLoader loader, UUID id) {
        var reader = new ConversationReader(id);
        loader.readConversation(reader, id);
        return reader.build();
    }
}
