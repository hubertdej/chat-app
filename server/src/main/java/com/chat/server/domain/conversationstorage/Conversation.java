package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;

import java.util.List;
import java.util.UUID;

record Conversation(UUID conversationId, String name, List<String> members, List<Message> messages) {
    ConversationDto dto() {
        return new ConversationDto(conversationId, name, members, messages.stream().map(Message::dto).toList());
    }
}
