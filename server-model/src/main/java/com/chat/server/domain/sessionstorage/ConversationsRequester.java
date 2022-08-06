package com.chat.server.domain.sessionstorage;

import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.messagereceiver.ListConversationsResponse;


public interface ConversationsRequester {
//    void sendMessage(MessageDto dto) throws MessagingSessionException;
    void forwardMessage(ListConversationsResponse dto) throws MessagingSessionException;
}
