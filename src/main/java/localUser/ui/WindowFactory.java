package localUser.ui;

import localUser.logic.ViewFactory;

public class WindowFactory implements ViewFactory {
    public ConversationWindow createConversationView() { return new ConversationWindow(); }
    public LoggedWindow createLoggedView() { return new LoggedWindow(); }
}
