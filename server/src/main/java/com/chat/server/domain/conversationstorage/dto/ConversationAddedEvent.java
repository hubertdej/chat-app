package com.chat.server.domain.conversationstorage.dto;

import java.util.List;
import java.util.UUID;

public record ConversationAddedEvent(
        UUID conversationId,
        String name,
        List<String> members) {
}
