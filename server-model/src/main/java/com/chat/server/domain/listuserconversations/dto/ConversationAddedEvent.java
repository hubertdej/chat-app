package com.chat.server.domain.listuserconversations.dto;

import com.chat.server.domain.conversationstorage.dto.MessageDto;

import java.util.List;
import java.util.UUID;

public record ConversationAddedEvent(
        UUID conversationId,
        String name,
        List<String> members,
        List<MessageDto> messages
) {
}
