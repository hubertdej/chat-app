package com.chat.database.records;

import java.sql.Timestamp;
import java.util.UUID;

public record DatabaseMessage(String from, UUID id, String content, Timestamp timestamp) { }
