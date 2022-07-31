package com.chat.client.fake;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonException extends RuntimeException {
    JsonException(JsonProcessingException cause) { super(cause); }
}
