package com.chat.client.database;


import com.chat.database.ConversationsEngine;
import com.chat.database.ConversationsLoader;

public interface InternalDatabaseFactory {
    ConversationsEngine getEngine(String username);
    ConversationsLoader getLoader(String username);
}
