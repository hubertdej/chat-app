package com.chat.server.domain.messagereceiver.dto;

import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.listuserconversations.dto.ListConversationsRequestDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use= JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({@JsonSubTypes.Type(MessageDto.class), @JsonSubTypes.Type(ListConversationsRequestDto.class)})
public class FromMessageDto {
    protected final String from;

    public FromMessageDto(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }
}


