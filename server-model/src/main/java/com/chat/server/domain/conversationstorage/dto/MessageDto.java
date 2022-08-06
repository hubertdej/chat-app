package com.chat.server.domain.conversationstorage.dto;

import com.chat.server.domain.messagereceiver.dto.FromMessageDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;
import java.util.UUID;

public class MessageDto extends FromMessageDto {
    private final UUID to;
    private final String content;
    private final Timestamp timestamp;

    @JsonCreator
    public MessageDto(
            @JsonProperty("from") String from,
            @JsonProperty("to") UUID to,
            @JsonProperty("content") String content,
            @JsonProperty("timestamp") Timestamp timestamp) {
        super(from);
        this.to = to;
        this.content = content;
        this.timestamp = timestamp;
    }

    public UUID getTo() {
        return to;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
