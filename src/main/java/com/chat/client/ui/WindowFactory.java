package com.chat.client.ui;

import com.chat.client.logic.ViewFactory;

public class WindowFactory implements ViewFactory {
    public ConversationWindow createConversationView() { return new ConversationWindow(); }
    public LoggedWindow createLoggedView() { return new LoggedWindow(); }
}
