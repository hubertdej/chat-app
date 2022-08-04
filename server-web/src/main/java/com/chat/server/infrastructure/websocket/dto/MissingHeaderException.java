package com.chat.server.infrastructure.websocket.dto;

public class MissingHeaderException extends IllegalArgumentException {
    public MissingHeaderException(String s) {
        super(s);
    }
}
