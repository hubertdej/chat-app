package com.chat.server.domain.listuserconversations.dto;

import java.util.List;
import java.util.UUID;

public record ConversationRemovedEvent(UUID conversationId, List<String> members) {
}
