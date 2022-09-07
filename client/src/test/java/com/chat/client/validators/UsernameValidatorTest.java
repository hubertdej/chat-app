package com.chat.client.validators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UsernameValidatorTest {
    UsernameValidator validator = new UsernameValidator();

    @Test
    void testAcceptValidUsernames() {
        assertDoesNotThrow(() -> validator.validate("abc"));
        assertDoesNotThrow(() -> validator.validate("abc135"));
    }

    @Test
    void testRejectInvalidUsernames() {
        assertThrows(ValidationException.class, () -> validator.validate(""));
        assertThrows(ValidationException.class, () -> validator.validate("asdf&*"));
    }
}
