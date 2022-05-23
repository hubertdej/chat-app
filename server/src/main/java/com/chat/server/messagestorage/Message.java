package com.chat.server.messagestorage;

import com.chat.server.messagestorage.dto.MessageDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class Message {
    String from;
    String content;
    Date date;
    MessageDto dto(){
        return new MessageDto(from, content, date);
    }
}
