package com.chat.server.domain.conversationstorage.dto;

import java.util.Date;
import java.util.UUID;

public record MessageDto(
        String from,
        UUID to,
        String content,
        Date date) {
}
