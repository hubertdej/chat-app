package com.chat.server.domain.conversationstorage.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class ConversationDto {
    private final UUID conversationId;
    private final String name;
    private final List<String> members;
    private final List<MessageDto> messages;

    @JsonCreator
    public ConversationDto(
            @JsonProperty("conversationId") UUID conversationId,
            @JsonProperty("name") String name,
            @JsonProperty("members")List<String> members,
            @JsonProperty("messages")List<MessageDto> messages) {
        this.conversationId = conversationId;
        this.name = name;
        this.members = members.stream().toList();
        this.messages = messages.stream().toList();
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public String getName() {
        return name;
    }

    public List<String> getMembers() {
        return members;
    }

    public List<MessageDto> getMessages() {
        return messages;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ConversationDto) obj;
        return Objects.equals(this.conversationId, that.conversationId) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.members, that.members) &&
                Objects.equals(this.messages, that.messages);
    }

    @Override
    public int hashCode() {
        return Objects.hash(conversationId, name, members, messages);
    }

    @Override
    public String toString() {
        return "ConversationDto[" +
                "conversationId=" + conversationId + ", " +
                "name=" + name + ", " +
                "members=" + members + ", " +
                "messages=" + messages + ']';
    }

}
