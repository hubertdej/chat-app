package com.chat.server.domain.messagereceiver;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.ConversationDto;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.listconversationids.dto.ListConversationsRequestDto;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.sessionstorage.ConversationsRequester;
import com.chat.server.domain.sessionstorage.MessagingSessionException;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;

import java.util.List;
import java.util.UUID;

public class MessageReceiverFacade {
    private final SessionStorageFacade sessionStorageFacade;
    private final ConversationStorageFacade conversationStorageFacade;
    private final ListUserConversationsFacade listUserConversationsFacade;

    public MessageReceiverFacade(SessionStorageFacade sessionStorageFacade,
                                 ConversationStorageFacade conversationStorageFacade,
                                 ListUserConversationsFacade listUserConversationsFacade) {
        this.sessionStorageFacade = sessionStorageFacade;
        this.conversationStorageFacade = conversationStorageFacade;
        this.listUserConversationsFacade = listUserConversationsFacade;
    }

    //TODO change so that returns ListConversationsResponse instead?
    public void receiveRequest(ConversationsRequester requester, ListConversationsRequestDto dto) throws MessagingSessionException {
        List<ConversationDto> conversationDtoList = listUserConversationsFacade.listConversations(dto);
        ListConversationsResponse listConversationsResponse = new ListConversationsResponse(conversationDtoList);
        requester.forwardMessage(listConversationsResponse);
    }

    public void receiveMessage(MessageDto dto) throws NoSuchConversationException {
        persist(dto);
        sessionStorageFacade.propagate(dto);
    }

    private void persist(MessageDto messageDto) throws NoSuchConversationException {
        UUID conversationId = messageDto.getTo();
        conversationStorageFacade.add(conversationId, messageDto);
    }
}
