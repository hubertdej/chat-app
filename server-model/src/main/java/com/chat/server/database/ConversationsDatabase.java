package com.chat.server.database;

import com.chat.server.database.common.ConversationsEngine;
import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationRemovedEvent;
import com.chat.server.domain.conversationstorage.dto.ConversationUpdatedEvent;
import com.chat.server.domain.conversationstorage.dto.MessageDto;

public class ConversationsDatabase implements ConversationStorageFacade.ConversationObserver {
    private final ConversationsEngine engine;

    public ConversationsDatabase(ConversationsEngine engine) {
        this.engine = engine;
    }

    @Override
    public void notifyUpdate(ConversationUpdatedEvent event) {
        if (event.isNewConversation()) {
            engine.addConversation(event.conversationId(), event.name());
            engine.addMembers(event.conversationId(), event.members());
        } else {
            MessageDto dto = event.newMessageDto();
            engine.addMessage(dto.from(), dto.to(), dto.content(), dto.timestamp().getTime());
        }
    }

    @Override
    public void notifyRemove(ConversationRemovedEvent event) {
        engine.removeConversation(event.conversationId());
    }
}
