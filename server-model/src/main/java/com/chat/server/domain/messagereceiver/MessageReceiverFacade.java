package com.chat.server.domain.messagereceiver;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.messagereceiver.dto.MessageReceivedDto;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;

import java.sql.Timestamp;
import java.util.UUID;

public class MessageReceiverFacade {
    private final SessionStorageFacade sessionStorageFacade;
    private final ConversationStorageFacade conversationStorageFacade;

    public MessageReceiverFacade(SessionStorageFacade sessionStorageFacade,
                                 ConversationStorageFacade conversationStorageFacade) {
        this.sessionStorageFacade = sessionStorageFacade;
        this.conversationStorageFacade = conversationStorageFacade;
    }


    public void receiveMessage(MessageReceivedDto messageReceivedDto) throws NoSuchConversationException {
        MessageDto messageDto = timestamp(messageReceivedDto);
        persist(messageDto);
        sessionStorageFacade.propagate(messageDto);
    }

    private MessageDto timestamp(MessageReceivedDto messageReceivedDto){
        return new MessageDto(messageReceivedDto.from(),messageReceivedDto.to(), messageReceivedDto.content(), new Timestamp(System.currentTimeMillis()));
    }

    private void persist(MessageDto messageDto) throws NoSuchConversationException {
        UUID conversationId = messageDto.to();
        conversationStorageFacade.add(conversationId, messageDto);
    }
}
