package com.chat.server.domain.listconversationids.dto;

import com.chat.server.domain.conversationstorage.dto.MessageDto;

import java.util.List;
import java.util.UUID;

public record ConversationPreviewDto(
        UUID conversationId,
        String name,
        List<String> members,
        MessageDto messageDto) {
}
