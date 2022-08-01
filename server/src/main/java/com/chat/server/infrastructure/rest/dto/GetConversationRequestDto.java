package com.chat.server.infrastructure.rest.dto;

import java.util.UUID;

public record GetConversationRequestDto(String username, String password, UUID conversationId) {
}
