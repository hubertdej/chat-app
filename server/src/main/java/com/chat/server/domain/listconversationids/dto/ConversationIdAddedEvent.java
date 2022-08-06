package com.chat.server.domain.listconversationids.dto;

import java.util.List;
import java.util.UUID;

public record ConversationIdAddedEvent(
        UUID conversationId,
        String name,
        List<String> members) {
}
