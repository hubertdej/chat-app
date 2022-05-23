package com.chat.server.messagestorage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MessageRepository  {
    void add(String channelId);
    void remove(String channelId);
    void save(String channelId, Message message);
    Page<Message> findAll(String channelId, Pageable pageable);
}

class InMemoryMessageRepository implements MessageRepository{
    Map<String, List<Message>> storage = new HashMap<>();

    @Override
    public void add(String channelId) {
        storage.putIfAbsent(channelId, new ArrayList<>());
    }

    @Override
    public void remove(String channelId) {
        storage.remove(channelId);
    }

    @Override
    public void save(String channelId, Message message) {
        storage.computeIfAbsent(channelId, str -> new ArrayList<>()).add(message);
    }

    @Override
    public Page<Message> findAll(String channelId, Pageable pageable) {
        List<Message> messages = storage.getOrDefault(channelId, List.of());
        long total = messages.size();
        return new PageImpl<>(messages, pageable, total);
    }
}
