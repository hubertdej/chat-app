package com.chat.server.domain.sessionstorage;

import com.chat.server.domain.messagereceiver.dto.ListConversationsResponse;


public interface ConversationsRequester {
//    void sendMessage(MessageDto dto) throws MessagingSessionException;
    void forwardMessage(ListConversationsResponse dto) throws MessagingSessionException;
}
