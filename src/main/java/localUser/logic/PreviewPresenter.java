package localUser.logic;

import localUser.model.Conversation;
import localUser.model.Preview;

public class PreviewPresenter {
    private final Preview preview;
    private final LoggedView view;
    private final OpeningFactory factory;



    PreviewPresenter(Preview preview, LoggedView view, OpeningFactory factory) {
        this.preview = preview;
        this.view = view;
        this.factory = factory;
    }

//    private static String itemDescription(Item item) {
//        return item.name() + ": " + item.count() + " at $" + item.price();
//    }
//    private void updateItem(Item item) {
//        view.setItem(store.items().indexOf(item), itemDescription(item));
//    }
    private final void addConversation(Conversation conversation) {
        view.addConversation(conversation.getName());
//        conversation.addObserver(itemObserver);
    }

    //        Button conversationButton = new Button();
//        conversationButton.setText("Open Conversation");
//        TextArea area = new TextArea();
//
//        Button sendButton = new Button();
//        sendButton.setText("send");
//        HBox typingBox = new HBox(10, area);
//        VBox conversationPane = new VBox(10);
//        conversationPane.getChildren().addAll(typingBox, sendButton);
//        Scene conversation = new Scene(conversationPane);
//        conversationButton.setOnAction(e -> {
//            stage.setTitle("Conversation");
//            stage.setScene(conversation);
//        });
//        HBox buttons = new HBox(10, conversationButton);
    public void openConversationScene(int index) {
        Conversation conversation = preview.getConversations().get(index);
        factory.openConversationView(conversation);
//        view.close();
    }
//
//    private final Item.Observer itemObserver = this::updateItem;
//    private final Store.Observer storeObserver = this::addItem;
//
    void open() {
        for (Conversation conversation : preview.getConversations()) {
            addConversation(conversation);
        }
//        preview.addObserver(storeObserver);
        view.open();
    }
    public void close() {
//        store.removeObserver(storeObserver);
//        for (Item item : store.items()) item.removeObserver(itemObserver);
        view.close();
    }
//
//    public void editItem(int index) {
//        factory.openEditView(store.items().get(index));
//    }
//    public void addNewItem() {
//        factory.openAddView(store);
//    }
}
