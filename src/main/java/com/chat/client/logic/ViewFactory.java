package com.chat.client.logic;

public interface ViewFactory {
    AuthenticationView createAuthenticationView();
    ConversationView createConversationView();
    LoggedView createLoggedView();
}
