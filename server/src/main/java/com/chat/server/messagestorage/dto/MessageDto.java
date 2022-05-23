package com.chat.server.messagestorage.dto;

import java.util.Date;

public record MessageDto(
        String from,
        String content,
        Date date) {
}
