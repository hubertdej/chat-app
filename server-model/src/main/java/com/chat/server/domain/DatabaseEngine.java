package com.chat.server.domain;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.registration.dto.UserDto;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public interface DatabaseEngine {
    interface IdsReader {
        void readId(UUID id);
    }
    interface UsersReader {
        void readUser(String username, String password);
    }
    interface ConversationReader {
        void readName(String name);
        void readMember(String username);
        void readMessage(String from, UUID to, String content, long timestampValue);
    }
    void addConversation(UUID uuid, String name);
    void addMembers(UUID conversationId, List<String> members);
    void addMessage(String from, UUID to, String content, Timestamp timestamp);
    void addUser(UserDto user);
    void removeConversation(UUID conversationId);
    void removeMember(UUID conversationId, String username);
    void readConversationIds(DatabaseEngine.IdsReader reader);
    void readConversation(DatabaseEngine.ConversationReader reader, UUID conversationId);
    void readUsers(DatabaseEngine.UsersReader reader);
}
