package localUser.logic;

import localUser.model.Conversation;
import localUser.model.Preview;

public class PresenterFactory implements OpeningFactory {
    private final ViewFactory viewFactory;

    public PresenterFactory(ViewFactory viewFactory) { this.viewFactory = viewFactory; }

//    public void openEditView(Item item) {
//        ItemView view = views.createItemView();
//        ItemPresenter presenter = new ItemPresenter(view);
//        view.initialize(presenter);
//        presenter.openEditView(item);
//    }
//    public void openAddView(Store store) {
//        ItemView view = views.createItemView();
//        ItemPresenter presenter = new ItemPresenter(view);
//        view.initialize(presenter);
//        presenter.openAddView(store);
//    }
//    public void openStoreView(Store store) {
//        StoreView view = views.createStoreView();
//        PreviewPresenter presenter = new PreviewPresenter(store, view, this);
//        view.initialize(presenter);
//        presenter.open();
//    }

    @Override
    public void openLoggedView(Preview preview) {
        LoggedView loggedView = viewFactory.createLoggedView();
        PreviewPresenter presenter = new PreviewPresenter(preview, loggedView, this);
        loggedView.initialize(presenter);
        presenter.open();
    }

    @Override
    public void openConversationView(Conversation conversation) {
        ConversationView conversationView = viewFactory.createConversationView();
        ConversationPresenter presenter = new ConversationPresenter(conversation, conversationView, this);
        conversationView.initialize(presenter);
        presenter.open();
    }
}
