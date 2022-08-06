package com.chat.server.domain.sessionstorage;

import com.chat.server.domain.conversationstorage.dto.MessageDto;
import com.chat.server.domain.messagereceiver.ListConversationsResponse;


public interface ServerMessagingSession {
    void sendMessage(MessageDto dto) throws MessagingSessionException;
    void sendMessage(ListConversationsResponse dto) throws MessagingSessionException;
}
