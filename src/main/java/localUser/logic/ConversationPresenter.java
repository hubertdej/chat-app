package localUser.logic;


import localUser.model.Conversation;
import localUser.model.Message;

import java.sql.Timestamp;

public class ConversationPresenter {

    private final Conversation conversation;
    private final ConversationView view;
    private final PresenterFactory factory;

    public ConversationPresenter(
            Conversation conversation,
            ConversationView conversationView,
            PresenterFactory presenterFactory
    ) {
        this.conversation = conversation;
        this.view = conversationView;
        this.factory = presenterFactory;
    }

    void open() {
        for (Message message : conversation.getMessages()) {
            addMessage(message);
        }
//        preview.addObserver(storeObserver);
        view.open();
    }

    public void close() {
//        store.removeObserver(storeObserver);
//        for (Item item : store.items()) item.removeObserver(itemObserver);
        view.close();
    }

    public String conversationName() {
        return conversation.getName();
    }

    public void sendMessage(String text) {
        conversation.getMessages().add(new Message(text, new Timestamp(System.currentTimeMillis())));
        /* serverAdapter.sendMessage() */
        System.out.println(text);
    }
    private void addMessage(Message message) {
        view.addMessage(message.getText());
//        conversation.addObserver(itemObserver);
    }


}
