package com.chat.client.validators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PasswordValidatorTest {
    PasswordValidator validator = new PasswordValidator();

    @Test
    void testAcceptValidPasswords() {
        assertDoesNotThrow(() -> validator.validate("abc"));
        assertDoesNotThrow(() -> validator.validate("abc135"));
    }

    @Test
    void testRejectInvalidPasswords() {
        assertThrows(ValidationException.class, () -> validator.validate(""));
        assertThrows(ValidationException.class, () -> validator.validate("asdf&*"));
    }
}
