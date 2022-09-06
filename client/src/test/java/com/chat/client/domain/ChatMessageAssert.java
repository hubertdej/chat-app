package com.chat.client.domain;

import org.assertj.core.api.AbstractAssert;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

public class ChatMessageAssert extends AbstractAssert<ChatMessageAssert, ChatMessage> {
    private ChatMessageAssert(ChatMessage ChatMessage) {
        super(ChatMessage, ChatMessageAssert.class);
    }
    
    public static ChatMessageAssert assertThat(ChatMessage ChatMessage) {
        return new ChatMessageAssert(ChatMessage);
    }

    public ChatMessageAssert hasData(UUID uuid, String text, User user, Timestamp timestamp, boolean sentByLocalUser) {
        if (!Objects.equals(actual.chatUUID(), uuid)) {
            failWithMessage("Expecting ChatMessage's chatUUID to be <%s> but was <%s>", actual.chatUUID(), uuid);
        }
        if (!actual.text().equals(text)) {
            failWithMessage("Expecting ChatMessage's text to be <%s> but was <%s>", actual.text(), text);
        }
        if (!actual.sender().equals(user)) {
            failWithMessage("Expecting ChatMessage's sender to be <%s> but was <%s>", actual.sender(), user);
        }
        if (!actual.timestamp().equals(timestamp)) {
            failWithMessage("Expecting ChatMessage's timestamp to be <%s> but was <%s>", actual.timestamp(), timestamp);
        }
        if (actual.sentByLocalUser() != sentByLocalUser) {
            failWithMessage("Expecting ChatMessage's sentByLocalUser to be <%s> but was <%s>", actual.sentByLocalUser(), sentByLocalUser);
        }
        return this;
    }
}

