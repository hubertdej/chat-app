package localUser.logic;

import localUser.model.Conversation;
import localUser.model.Preview;

interface OpeningFactory {
    void openLoggedView(Preview preview);
    void openConversationView(Conversation conversation);
//        void openConversationView(Conversation conversation);
//        void startConversation(Object o);
}