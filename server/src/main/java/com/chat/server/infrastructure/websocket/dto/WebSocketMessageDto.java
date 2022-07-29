package com.chat.server.infrastructure.websocket.dto;

import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.listconversationids.dto.ListConversationsRequestDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use= JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({@JsonSubTypes.Type(MessageDto.class), @JsonSubTypes.Type(ListConversationsRequestDto.class)})
public class WebSocketMessageDto {
    protected final String from;

    public WebSocketMessageDto(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }
}


