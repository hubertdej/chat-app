package com.chat.server.infrastructure.rest.dto;

import java.util.List;

public record AddConversationRequestDto(
        String name,
        List<String> members
) {

}
