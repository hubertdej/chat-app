package com.chat.client.logic;

public interface ViewFactory {
    ConversationView createConversationView();
    LoggedView createLoggedView();
}
