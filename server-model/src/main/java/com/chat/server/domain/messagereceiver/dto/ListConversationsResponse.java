package com.chat.server.domain.messagereceiver.dto;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;

import java.util.List;

public record ListConversationsResponse(List<ConversationDto> conversations) {
}
