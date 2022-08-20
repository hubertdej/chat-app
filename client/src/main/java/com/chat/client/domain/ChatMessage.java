package com.chat.client.domain;

import java.sql.Timestamp;
import java.util.UUID;

public record ChatMessage(UUID chatUUID, String text, User sender, Timestamp timestamp, boolean sentByLocalUser) {
}
