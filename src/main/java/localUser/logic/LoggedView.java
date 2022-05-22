package localUser.logic;

public interface LoggedView {
    void initialize(PreviewPresenter presenter);
    void addConversation(String name);
    void open();
    void close();
}
