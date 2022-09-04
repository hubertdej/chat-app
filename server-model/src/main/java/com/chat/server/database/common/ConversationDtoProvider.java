package com.chat.server.database.common;

import com.chat.server.domain.conversationstorage.dto.ConversationDto;

import java.util.UUID;

public class ConversationDtoProvider {
    public ConversationDto provideDto(ConversationsLoader loader, UUID id) {
        var reader = new ConversationReader(id);
        loader.readConversation(reader, id);
        return reader.build();
    }
}
