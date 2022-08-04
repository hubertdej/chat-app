package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.MessageDto;

import java.sql.Timestamp;
import java.util.UUID;

record Message (String from,
                       UUID to,
                       String content,
                       Timestamp timestamp){
    MessageDto dto(){
        return new MessageDto(from, to, content, timestamp);
    }
}
