package com.chat.server.domain.conversationstorage.dto;

import java.util.List;
import java.util.UUID;

public record ConversationRemovedEvent(UUID conversationId,
                                       List<String> members) {
}
