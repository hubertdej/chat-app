package com.chat.server.persistency;

public class SqlEngineFactory {
    public static SqlEngine getDatabase(String path) {
        var database = new SqlEngine(path);
        database.init();
        return database;
    }
}
