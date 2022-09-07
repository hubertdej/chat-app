package com.chat.client.validators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChatNameValidatorTest {
    ChatNameValidator validator = new ChatNameValidator();

    @Test
    void testAcceptValidNames() {
        assertDoesNotThrow(() -> validator.validate("abc def"));
        assertDoesNotThrow(() -> validator.validate("Abc"));
    }

    @Test
    void testRejectInvalidNames() {
        assertThrows(ValidationException.class, () -> validator.validate("asdf2938"));
        assertThrows(ValidationException.class, () -> validator.validate(" asdf"));
        assertThrows(ValidationException.class, () -> validator.validate("asdf "));
    }
}
