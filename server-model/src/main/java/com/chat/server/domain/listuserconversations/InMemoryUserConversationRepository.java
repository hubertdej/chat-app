package com.chat.server.domain.listuserconversations;


import com.chat.server.domain.conversationstorage.dto.ConversationDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

interface UserConversationRepository {
    void add(String username, ConversationDto conversation);
    void remove(String username, UUID conversationId);
    List<ConversationDto> get(String username);
}

public class InMemoryUserConversationRepository implements UserConversationRepository {
    Map<String, Map<UUID, ConversationDto>> storage = new HashMap<>();
    @Override
    public void add(String username, ConversationDto conversationDto) {
        storage.computeIfAbsent(username, u -> new HashMap<>()).put(conversationDto.getConversationId(), conversationDto);
    }
    @Override
    public void remove(String username, UUID conversationId) {
        storage.computeIfPresent(username,
                (uname, conversations) -> {
                    conversations.remove(conversationId);
                    return conversations;
                });
    }
    //TODO storage.get(username) is null AFTER user registration
    @Override
    public List<ConversationDto> get(String username) {
        return storage.get(username).values().stream().toList();
    }
}
