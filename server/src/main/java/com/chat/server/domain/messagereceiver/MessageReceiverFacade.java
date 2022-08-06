package com.chat.server.domain.messagereceiver;

import com.chat.server.domain.conversationstorage.ConversationStorageFacade;
import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.conversationstorage.dto.NoSuchConversationException;
import com.chat.server.domain.listuserconversations.ListUserConversationsFacade;
import com.chat.server.domain.sessionstorage.SessionStorageFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
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

    public void receiveMessage(WebSocketSession session, MessageDto messageDto) throws NoSuchConversationException, IOException {
        persist(messageDto);
        sessionStorageFacade.propagate(messageDto);
//        } else if(messageDto1 instanceof ListConversationsRequestDto listConversationsRequestDto){
//            List<ConversationDto> conversationDtoList = listUserConversationsFacade.listConversations(listConversationsRequestDto);
//            ListConversationsResponse listConversationsResponse = new ListConversationsResponse(conversationDtoList);
//            String jsonResponse = objectMapper.writeValueAsString(listConversationsResponse);
//            session.sendMessage(new TextMessage(jsonResponse));
//        }
    }

    private void persist(MessageDto messageDto) throws NoSuchConversationException {
        UUID conversationId = messageDto.getTo();
        conversationStorageFacade.add(conversationId, messageDto);
    }


}
