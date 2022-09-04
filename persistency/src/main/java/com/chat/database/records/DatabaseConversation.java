package com.chat.database.records;

import java.util.List;
import java.util.UUID;

public record DatabaseConversation(UUID id, String name, List<String> members, List<DatabaseMessage> messages) { }
