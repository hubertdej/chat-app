package com.chat.server.domain.listconversationids.dto;

import java.util.List;
import java.util.UUID;

public record ConversationIdRemovedEvent(UUID conversationId,
                                         List<String> members) {
}
