package com.chat.server.domain.sessionstorage;

import java.io.IOException;

public class MessagingSessionException extends Exception {
    public MessagingSessionException(IOException cause) { super(cause); }
}
