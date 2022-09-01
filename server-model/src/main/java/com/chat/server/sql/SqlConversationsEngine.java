package com.chat.server.sql;

import com.chat.server.database.common.ConversationsEngine;
import com.chat.server.database.common.ConversationsLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class SqlConversationsEngine implements ConversationsEngine {
    private final String url;

    public SqlConversationsEngine(String path) {
        this.url = "jdbc:sqlite:" + path;
    }

    @Override
    public void addConversation(UUID uuid, String name) {
        try (Connection connection = DriverManager.getConnection(url)) {
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
        try (Connection connection = DriverManager.getConnection(url)) {
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
        try (Connection connection = DriverManager.getConnection(url)) {
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
    public void removeConversation(UUID conversationId) {
        try (Connection connection = DriverManager.getConnection(url)) {
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
        try (Connection connection = DriverManager.getConnection(url)) {
            PreparedStatement membershipDelete = connection.prepareStatement("delete from membership " +
                    "where username = ? and conversation_id = ?");
            membershipDelete.setString(1, username);
            membershipDelete.setString(2, conversationId.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
