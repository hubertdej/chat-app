package com.chat.server.domain.messagereceiver;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;

import java.util.List;

record ListConversationsResponse(List<ConversationDto> conversations) {
}
