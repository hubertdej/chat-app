package com.chat.server.domain.listconversations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

interface ConversationRepository {

    void add(String username, UUID conversationId);

    void remove(String username, UUID conversationId);

    List<UUID> get(String username);
}

class InMemoryConversationRepository implements ConversationRepository{
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
