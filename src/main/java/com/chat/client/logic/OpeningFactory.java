package com.chat.client.logic;

import com.chat.client.model.Preview;
import com.chat.client.model.Conversation;

interface OpeningFactory {
    void openLoggedView(Preview preview);
    void openConversationView(Conversation conversation);
//        void openConversationView(Conversation conversation);
//        void startConversation(Object o);
}
