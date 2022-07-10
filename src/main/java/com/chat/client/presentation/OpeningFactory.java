package com.chat.client.presentation;

import com.chat.client.domain.Preview;
import com.chat.client.domain.Conversation;

interface OpeningFactory {
    void openLoggedView(Preview preview);
    void openConversationView(Conversation conversation);
//        void openConversationView(Conversation conversation);
//        void startConversation(Object o);
}
