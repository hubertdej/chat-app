package com.chat.server.sql;

import com.chat.server.database.ConversationsEngine;
import com.chat.server.database.ConversationsLoader;
import com.chat.server.database.UsersEngine;
import com.chat.server.database.UsersLoader;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class SqlEngine implements ConversationsEngine, ConversationsLoader, UsersEngine, UsersLoader {
    private final String path;

    SqlEngine(String path) {
        this.path = path;
    }
    void init() {
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

    @Override
    public void addConversation(UUID uuid, String name) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement conversationsInsert = connection.prepareStatement("insert into conversations" +
                    "(conversation_id, name) " +
                    "values (?, ?)");
            conversationsInsert.setString(1, uuid.toString());
            conversationsInsert.setString(2, name);
            conversationsInsert.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void addMembers(UUID conversationId, List<String> members) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement membershipInsert = connection.prepareStatement("insert into membership" +
                    "(username, conversation_id) " +
                    "values (?, ?)");
            membershipInsert.setString(2, conversationId.toString());
            for (String member : members) {
                membershipInsert.setString(1, member);
                membershipInsert.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void addMessage(String from, UUID to, String content, long timestampValue) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement messageInsert = connection.prepareStatement("insert into messages" +
                    "(timestamp, sender, conversation_id, message_content) " +
                    "values (?, ?, ?, ?)");
            messageInsert.setLong(1, timestampValue);
            messageInsert.setString(2, from);
            messageInsert.setString(3, to.toString());
            messageInsert.setString(4, content);
            messageInsert.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void addUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement userInsert = connection.prepareStatement("insert into users" +
                    "(username, password) " +
                    "values (?, ?)");
            userInsert.setString(1, username);
            userInsert.setString(2, password);
            userInsert.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void removeConversation(UUID conversationId) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement messagesDelete = connection.prepareStatement("delete from messages " +
                    "where conversation_id = ?");
            messagesDelete.setString(1, conversationId.toString());
            PreparedStatement membershipDelete = connection.prepareStatement("delete from membership " +
                    "where conversation_id = ?");
            membershipDelete.setString(1, conversationId.toString());
            PreparedStatement conversationDelete = connection.prepareStatement("delete from conversations " +
                    "where conversation_id = ?");
            conversationDelete.setString(1, conversationId.toString());

            messagesDelete.executeUpdate();
            membershipDelete.executeUpdate();
            conversationDelete.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void removeMember(UUID conversationId, String username) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement membershipDelete = connection.prepareStatement("delete from membership " +
                    "where username = ? and conversation_id = ?");
            membershipDelete.setString(1, username);
            membershipDelete.setString(2, conversationId.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void readConversationIds(ConversationsEngine.IdsReader reader) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement idsSelect = connection.prepareStatement("select conversation_id from conversations");
            var ids = idsSelect.executeQuery();
            while (ids.next()) {
                UUID conversation_id = UUID.fromString(ids.getString("conversation_id"));
                reader.readId(conversation_id);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void readConversation(ConversationsEngine.ConversationReader reader, UUID conversationId) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement conversationSelect = connection.prepareStatement("select name from conversations " +
                    "where conversation_id = ?");
            conversationSelect.setString(1, conversationId.toString());
            PreparedStatement membersSelect = connection.prepareStatement("select username from membership " +
                    "where conversation_id = ?");
            membersSelect.setString(1, conversationId.toString());
            PreparedStatement messagesSelect = connection.prepareStatement("select * from messages");

            var nameResult = conversationSelect.executeQuery();
            var membersResult = membersSelect.executeQuery();
            var messagesResult = messagesSelect.executeQuery();

            nameResult.next();
            reader.readName(nameResult.getString("name"));
            while (membersResult.next()) {
                String username = membersResult.getString("username");
                reader.readMember(username);
            }
            while (messagesResult.next()) {
                long timestampValue = messagesResult.getLong("timestamp");
                String sender = messagesResult.getString("sender");
                UUID conversation_id = UUID.fromString(messagesResult.getString("conversation_id"));
                String content = messagesResult.getString("message_content");
                reader.readMessage(sender, conversation_id, content, timestampValue);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void readUsers(UsersLoader.UsersReader reader) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement usersSelect = connection.prepareStatement("select * from users");
            var users = usersSelect.executeQuery();
            while (users.next()) {
                String username = users.getString("username");
                String password = users.getString("password");
                reader.readUser(username, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
