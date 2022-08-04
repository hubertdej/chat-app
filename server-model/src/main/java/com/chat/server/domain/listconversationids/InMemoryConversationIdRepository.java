package com.chat.server.domain.listconversationids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class InMemoryConversationIdRepository implements ConversationIdRepository {
    //TODO use set instead of list
    private final HashMap<String, List<UUID>> storage = new HashMap<>();

    @Override
    public void add(String username, UUID conversationId) {
        storage.computeIfAbsent(username, u -> new ArrayList<>()).add(conversationId);
    }

    @Override
    public void remove(String username, UUID conversationId) {
        storage.computeIfPresent(
                username,
                (uname, currentConversations) -> {
                    currentConversations.remove(conversationId);
                    return currentConversations;
                });
    }

    @Override
    public List<UUID> get(String username) {
        return storage.get(username);
    }
}
