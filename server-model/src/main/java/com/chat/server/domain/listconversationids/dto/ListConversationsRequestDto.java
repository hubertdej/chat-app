package com.chat.server.domain.listconversationids.dto;

import com.chat.server.domain.messagereceiver.dto.FromMessageDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

public class ListConversationsRequestDto extends FromMessageDto {
    private final Map<UUID, Timestamp> lastMessage;

    @JsonCreator
    public ListConversationsRequestDto(
            @JsonProperty("from") String from,
            @JsonProperty("lastMessage") Map<UUID, Timestamp> lastMessage) {
        super(from);
        this.lastMessage = lastMessage == null ? Map.of() : lastMessage;
    }

    public Map<UUID, Timestamp> getLastMessage() {
        return lastMessage;
    }
}
