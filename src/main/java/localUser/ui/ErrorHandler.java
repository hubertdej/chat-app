package localUser.ui;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.control.Alert;

class ErrorHandler implements EventDispatcher {
    private final EventDispatcher original;

    ErrorHandler(EventDispatcher original) { this.original = original; }

    public Event dispatchEvent(Event event, EventDispatchChain tail) {
        try { return original.dispatchEvent(event, tail); }
        catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.toString());
            alert.setHeaderText(null);
            alert.showAndWait();
            return null;
        }
    }
}
