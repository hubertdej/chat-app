package com.chat.server.domain.listuserconversations.dto;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

public record ListConversationsRequestDto(String from, Map<UUID, Timestamp> lastMessage) { }
