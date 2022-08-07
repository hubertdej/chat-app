package com.chat.client.network;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonException extends RuntimeException {
    JsonException(JsonProcessingException cause) { super(cause); }
}
