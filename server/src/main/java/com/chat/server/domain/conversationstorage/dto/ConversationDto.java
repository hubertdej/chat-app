package com.chat.server.domain.conversationstorage.dto;


import com.chat.server.domain.listconversations.dto.ConversationPreviewDto;

import java.util.List;
import java.util.UUID;

public record ConversationDto(UUID conversationId,
                             String name,
                             List<String> members,
                             List<MessageDto> messages) {
    public ConversationPreviewDto conversationPreviewDto(){
        MessageDto lastMessage = messages.size() > 0 ? messages.get(messages().size()-1) : null;
        return new ConversationPreviewDto(conversationId, name, members, lastMessage);
    }
}
