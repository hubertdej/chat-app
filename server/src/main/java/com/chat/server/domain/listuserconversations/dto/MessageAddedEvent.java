package com.chat.server.domain.listuserconversations.dto;

import com.chat.server.domain.conversationstorage.dto.MessageDto;

import java.util.List;

public record MessageAddedEvent(MessageDto messageDto, List<String> members) {
}
