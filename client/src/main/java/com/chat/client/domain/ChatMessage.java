package com.chat.client.domain;

import java.sql.Timestamp;

public record ChatMessage(String text, User sender, Timestamp timestamp) {
}
