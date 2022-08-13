package com.chat.server.domain.conversationstorage.dto;

import java.sql.Timestamp;
import java.util.UUID;

public record MessageDto(String from, UUID to, String content, Timestamp timestamp) { }
