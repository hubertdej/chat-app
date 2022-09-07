package com.chat.client.validators;

public class UsernameValidator extends RegexValidator {
    public UsernameValidator() {
        super("[a-z0-9]+", "Username must be non-empty alphanumeric string.");
    }
}
