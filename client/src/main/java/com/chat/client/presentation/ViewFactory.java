package com.chat.client.presentation;

public interface ViewFactory {
    AuthView createAuthView();
    ConversationView createConversationView();
    LoggedView createLoggedView();
    ChatlistView createChatlistView();
    CreationView createCreationView();
    ChatView createChatView();
}