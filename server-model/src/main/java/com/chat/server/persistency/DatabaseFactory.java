package com.chat.server.persistency;

public class DatabaseFactory {
    public static Database getDatabase(String path) {
        var database = new Database(path);
        database.init();
        return database;
    }
}
