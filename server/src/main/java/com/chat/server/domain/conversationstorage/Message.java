package com.chat.server.domain.conversationstorage;

import com.chat.server.domain.conversationstorage.dto.MessageDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.UUID;

public record Message (String from,
                       UUID to,
                       String content,
                       Date date){
    MessageDto dto(){
        return new MessageDto(from, to, content, date);
    }
}
