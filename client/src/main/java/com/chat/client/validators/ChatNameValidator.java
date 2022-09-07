package com.chat.client.validators;

public class ChatNameValidator extends RegexValidator {
    public ChatNameValidator() {
        super("([a-zA-Z][a-zA-Z ]*[a-zA-Z])", "Chat name must be a non-empty sequence containing only letters and spaces");
    }
}
