package com.chat.client.presentation;

public interface ViewFactory {
    AuthenticationView createAuthenticationView();
    ConversationView createConversationView();
    LoggedView createLoggedView();
}
