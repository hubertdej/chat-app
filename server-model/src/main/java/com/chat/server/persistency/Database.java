package com.chat.server.persistency;


import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.registration.dto.UserDto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Database {
    private final String path;

    Database(String path) {
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

    public void createConversation(UUID uuid, String name) {
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

    public void addMessage(MessageDto message) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement messageInsert = connection.prepareStatement("insert into messages" +
                    "(timestamp, sender, conversation_id, message_content) " +
                    "values (?, ?, ?, ?)");
            messageInsert.setLong(1, message.timestamp().getTime());
            messageInsert.setString(2, message.from());
            messageInsert.setString(3, message.to().toString());
            messageInsert.setString(4, message.content());
            messageInsert.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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

    public void saveUser(UserDto user) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement userInsert = connection.prepareStatement("insert into users" +
                    "(username, password) " +
                    "values (?, ?)");
            userInsert.setString(1, user.username());
            userInsert.setString(2, user.password());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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

    public List<UUID> readConversationIds() {
        List<UUID> idsList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement idsSelect = connection.prepareStatement("select conversation_id from conversations");
            var ids = idsSelect.executeQuery();
            while (ids.next()) {
                UUID conversation_id = UUID.fromString(ids.getString("conversation_id"));
                idsList.add(conversation_id);
            }
            return idsList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UserDto> readUsers() {
        List<UserDto> usersList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement usersSelect = connection.prepareStatement("select * from users");
            var users = usersSelect.executeQuery();
            while (users.next()) {
                String username = users.getString("username");
                String password = users.getString("password");
                usersList.add(new UserDto(username, password));
            }
            return usersList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ConversationDto readConversation(UUID conversationId) {
        String name = null;
        List<String> members = new ArrayList<>();
        List<MessageDto> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + path)) {
            PreparedStatement conversationSelect = connection.prepareStatement("select name from membership " +
                    "where conversation_id = ?");
            conversationSelect.setString(1, conversationId.toString());
            PreparedStatement membersSelect = connection.prepareStatement("select users from membership " +
                    "where conversation_id = ?");
            membersSelect.setString(1, conversationId.toString());
            PreparedStatement messagesSelect = connection.prepareStatement("select * from messages");

            var nameResult = conversationSelect.executeQuery();
            var membersResult = membersSelect.executeQuery();
            var messagesResult = membersSelect.executeQuery();

            nameResult.next();
            name = nameResult.getString("name");
            while (membersResult.next()) {
                String username = membersResult.getString("username");
                members.add(username);
            }
            while (messagesResult.next()) {
                Long timestamp = messagesResult.getLong("timestamp");
                String sender = messagesResult.getString("sender");
                UUID conversation_id = UUID.fromString(messagesResult.getString("conversation_id"));
                String content = messagesResult.getString("message_content");
                messages.add(new MessageDto(sender, conversation_id, content, new Timestamp(timestamp)));
            }

            return new ConversationDto(conversationId, name, members, messages);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
