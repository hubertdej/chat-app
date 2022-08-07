package com.chat.client.presentation;

public interface ViewFactory {
    AuthView createAuthView();
    ChatlistView createChatlistView();
    CreationView createCreationView();
    ChatView createChatView();
}
