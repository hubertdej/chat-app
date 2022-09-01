package com.chat.server.sql;

import com.chat.server.database.common.ConversationsLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SqlConversationsLoader implements ConversationsLoader {
    private final String url;

    public SqlConversationsLoader(String path) {
        this.url = "jdbc:sqlite:" + path;
    }
    @Override
    public void readConversationIds(ConversationsLoader.IdsReader reader) {
        try (Connection connection = DriverManager.getConnection(url)) {
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
    public void readConversation(ConversationsLoader.ConversationReader reader, UUID conversationId) {
        try (Connection connection = DriverManager.getConnection(url)) {
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
}
