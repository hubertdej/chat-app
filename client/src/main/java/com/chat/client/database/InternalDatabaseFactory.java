package com.chat.client.database;

import com.chat.server.database.common.ConversationsEngine;
import com.chat.server.database.common.ConversationsLoader;

public interface InternalDatabaseFactory {
    ConversationsEngine getEngine(String username);
    ConversationsLoader getLoader(String username);
}
