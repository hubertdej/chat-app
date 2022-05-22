package localUser.logic;

public interface ConversationView {
    void initialize(ConversationPresenter presenter);

    void addMessage(String text);
    void open();
    void close();
}
