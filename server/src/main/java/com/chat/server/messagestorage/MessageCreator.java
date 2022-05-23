package com.chat.server.messagestorage;

import com.chat.server.messagestorage.dto.MessageDto;

class MessageCreator {
    Message create(MessageDto dto){
        return new Message(dto.from(), dto.content(), dto.date());
    }
}
