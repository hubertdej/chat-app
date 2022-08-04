package com.chat.server.domain.listconversationids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface ConversationIdRepository {

    void add(String username, UUID conversationId);

    void remove(String username, UUID conversationId);

    List<UUID> get(String username);
}

