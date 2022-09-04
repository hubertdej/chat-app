package com.chat.server.sql;

import com.chat.database.ConversationsEngine;
import com.chat.database.ConversationsLoader;
import com.chat.database.UsersEngine;
import com.chat.database.UsersLoader;
import com.chat.sql.SqlConversationsEngine;
import com.chat.sql.SqlConversationsLoader;
import com.chat.sql.SqlUsersEngine;
import com.chat.sql.SqlUsersLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlFactory {
    public static ConversationsEngine getConversationsEngine(String path) {
        var engine = new SqlConversationsEngine(path);
        init(path);
        return engine;
    }
    public static ConversationsLoader getConversationsLoader(String path) {
        var loader = new SqlConversationsLoader(path);
        init(path);
        return loader;
    }
    public static UsersEngine getUsersEngine(String path) {
        var engine = new SqlUsersEngine(path);
        init(path);
        return engine;
    }
    public static UsersLoader getUsersLoader(String path) {
        var loader = new SqlUsersLoader(path);
        init(path);
        return loader;
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
