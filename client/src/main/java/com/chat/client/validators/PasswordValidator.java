package com.chat.client.validators;

public class PasswordValidator extends RegexValidator {
    public PasswordValidator() {
        super("[a-z0-9]+", "Password must be non-empty alphanumeric string.");
    }
}
