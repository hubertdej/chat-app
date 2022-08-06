package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.MessageDto;

class MessageCreator {
    Message create(MessageDto dto){
        return new Message(dto.getFrom(), dto.getTo(), dto.getContent(), dto.getTimestamp());
    }
}
