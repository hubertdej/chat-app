package com.chat.server.domain.listuserconversations;


import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;

import java.util.*;

public interface UserConversationRepository {
    void add(String username, ConversationDto conversation);
    void addMessage(MessageDto messageDto, List<String> members);
    void remove(String username, UUID conversationId);
    List<ConversationDto> get(String username);
}

