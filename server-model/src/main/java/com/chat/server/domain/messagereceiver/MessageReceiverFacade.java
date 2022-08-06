package com.chat.server.domain.messagereceiver;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.listconversationids.dto.ListConversationsRequestDto;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.messagereceiver.dto.FromMessageDto;
import com.chat.server.domain.sessionstorage.MessagingSessionException;
import com.chat.server.domain.sessionstorage.ServerMessagingSession;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class MessageReceiverFacade {
    private final SessionStorageFacade sessionStorageFacade;
    private final ConversationStorageFacade conversationStorageFacade;
    private final ListUserConversationsFacade listUserConversationsFacade;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MessageReceiverFacade(SessionStorageFacade sessionStorageFacade,
                                 ConversationStorageFacade conversationStorageFacade,
                                 ListUserConversationsFacade listUserConversationsFacade) {
        this.sessionStorageFacade = sessionStorageFacade;
        this.conversationStorageFacade = conversationStorageFacade;
        this.listUserConversationsFacade = listUserConversationsFacade;
    }

    public void receiveMessage(ServerMessagingSession session, FromMessageDto webSocketMessageDto) throws NoSuchConversationException, IOException, MessagingSessionException {
        if(webSocketMessageDto instanceof MessageDto messageDto){
            persist(messageDto);
            sessionStorageFacade.propagate(messageDto);
        } else if(webSocketMessageDto instanceof ListConversationsRequestDto listConversationsRequestDto){
            List<ConversationDto> conversationDtoList = listUserConversationsFacade.listConversations(listConversationsRequestDto);
            ListConversationsResponse listConversationsResponse = new ListConversationsResponse(conversationDtoList);
            session.sendMessage(listConversationsResponse);
        }
    }

    private void persist(MessageDto messageDto) throws NoSuchConversationException {
        UUID conversationId = messageDto.getTo();
        conversationStorageFacade.add(conversationId, messageDto);
    }


}
