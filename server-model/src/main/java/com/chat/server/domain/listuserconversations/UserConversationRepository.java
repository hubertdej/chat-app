package com.chat.server.domain.listuserconversations;


import com.chat.server.domain.conversationstorage.dto.ConversationDto;

import java.util.*;

public interface UserConversationRepository {
    void add(String username, ConversationDto conversation);
    void remove(String username, UUID conversationId);
    List<ConversationDto> get(String username);
}

