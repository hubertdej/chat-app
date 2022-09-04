package com.chat.client.sql;

import com.chat.client.database.InternalDatabaseFactory;
import com.chat.database.ConversationsEngine;
import com.chat.database.ConversationsLoader;
import com.chat.sql.SqlConversationsEngine;
import com.chat.sql.SqlConversationsLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlInternalFactory implements InternalDatabaseFactory {
    @Override
    public ConversationsEngine getEngine(String username) {
        String path = buildPath(username);
        initDatabase(path);
        return new SqlConversationsEngine(path);
    }

    @Override
    public ConversationsLoader getLoader(String username) {
        String path = buildPath(username);
        initDatabase(path);
        return new SqlConversationsLoader(path);
    }
    void initDatabase(String path) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement enableForeignKeys = connection.prepareStatement(
                    "pragma foreign_keys = 1"
            );
            PreparedStatement createConversations = connection.prepareStatement(
                    "create table if not exists conversations (" +
                            "conversation_id text primary key, " +
                            "name text not null" +
                            ")"
            );
            PreparedStatement createMembership = connection.prepareStatement(
                    "create table if not exists membership (" +
                            "username text not null," +
                            "conversation_id text not null references conversations(conversation_id)" +
                            ")"
            );
            PreparedStatement createMessages = connection.prepareStatement(
                    "create table if not exists messages (" +
                            "timestamp integer primary key, " +
                            "sender text not null, " +
                            "conversation_id text not null references conversations(conversation_id), " +
                            "message_content text not null" +
                            ")"
            );

            enableForeignKeys.executeUpdate();
            createConversations.executeUpdate();
            createMembership.executeUpdate();
            createMessages.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private String buildPath(String username) {
        return username + "-chats.db";
    }
}
