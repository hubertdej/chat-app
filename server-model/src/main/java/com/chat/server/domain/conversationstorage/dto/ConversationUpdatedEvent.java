package com.chat.server.domain.conversationstorage.dto;

import java.util.List;
import java.util.UUID;

public record ConversationUpdatedEvent(UUID conversationId, String name, List<String> members, MessageDto newMessageDto) {
    public boolean isNewConversation(){
        return newMessageDto == null;
    }
}
