package com.chat.client.sql;

import com.chat.server.database.common.ConversationsEngine;
import com.chat.server.database.common.ConversationsLoader;

import java.util.List;
import java.util.UUID;

public class SqlEngine implements ConversationsEngine, ConversationsLoader {
    @Override
    public void addConversation(UUID uuid, String name) {

    }

    @Override
    public void addMembers(UUID conversationId, List<String> members) {

    }

    @Override
    public void addMessage(String from, UUID to, String content, long timestamp) {

    }

    @Override
    public void removeConversation(UUID conversationId) {

    }

    @Override
    public void removeMember(UUID conversationId, String username) {

    }

    @Override
    public void readConversationIds(IdsReader reader) {

    }

    @Override
    public void readConversation(ConversationReader reader, UUID conversationId) {

    }
}
