package com.chat.server.domain.listuserconversations.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

public class ListConversationsRequestDto {
    private final String from;
    private final Map<UUID, Timestamp> lastMessage;

    @JsonCreator
    public ListConversationsRequestDto(
            @JsonProperty("from") String from,
            @JsonProperty("lastMessage") Map<UUID, Timestamp> lastMessage) {
        this.from = from;
        this.lastMessage = lastMessage == null ? Map.of() : lastMessage;
    }

    public Map<UUID, Timestamp> getLastMessage() {
        return lastMessage;
    }

    public String getFrom() {
        return from;
    }
}
