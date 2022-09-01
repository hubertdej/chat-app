package com.chat.server.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlFactory {
    public static SqlConversationsEngine getConversationsEngine(String path) {
        var database = new SqlConversationsEngine(path);
        init(path);
        return database;
    }
    public static SqlConversationsLoader getConversationsLoader(String path) {
        var database = new SqlConversationsLoader(path);
        init(path);
        return database;
    }
    public static SqlUsersManager getUsersManager(String path) {
        var database = new SqlUsersManager(path);
        init(path);
        return database;
    }
    static void init(String path) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement enableForeignKeys = connection.prepareStatement(
                    "pragma foreign_keys = 1"
            );

            PreparedStatement createUsers = connection.prepareStatement(
                    "create table if not exists users (" +
                            "username text primary key, " +
                            "password text not null" +
                            ")"
            );
            PreparedStatement createConversations = connection.prepareStatement(
                    "create table if not exists conversations (" +
                            "conversation_id text primary key, " +
                            "name text not null" +
                            ")"
            );
            PreparedStatement createMembership = connection.prepareStatement(
                    "create table if not exists membership (" +
                            "username text not null references users(username), " +
                            "conversation_id text not null references conversations(conversation_id)" +
                            ")"
            );
            PreparedStatement createMessages = connection.prepareStatement(
                    "create table if not exists messages (" +
                            "timestamp integer primary key, " +
                            "sender text not null references users(username), " +
                            "conversation_id text not null references conversations(conversation_id), " +
                            "message_content text not null" +
                            ")"
            );
            enableForeignKeys.executeUpdate();
            createUsers.executeUpdate();
            createConversations.executeUpdate();
            createMembership.executeUpdate();
            createMessages.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
