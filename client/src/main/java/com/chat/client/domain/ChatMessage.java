package com.chat.client.domain;

public record ChatMessage(String text, User sender) {
}
