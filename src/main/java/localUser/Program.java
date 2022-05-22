package localUser;

import localUser.logic.PresenterFactory;
import localUser.model.Preview;
import localUser.ui.Gui;
import localUser.ui.WindowFactory;

class Program {
    public static void main(String[] args) {
        Preview local = new Preview();
        Gui.run(() -> new PresenterFactory(new WindowFactory()).openLoggedView(local));
    }
}
