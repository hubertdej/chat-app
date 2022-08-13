package com.chat.server.domain.messagereceiver.dto;

import java.util.UUID;

public record MessageReceivedDto(String from, UUID to, String content) {
}
