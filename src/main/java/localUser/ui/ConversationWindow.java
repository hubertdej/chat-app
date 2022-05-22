package localUser.ui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import localUser.logic.ConversationPresenter;
import localUser.logic.ConversationView;

import java.sql.Timestamp;

public class ConversationWindow implements ConversationView {
    private Stage stage;
    private Button sendButton;
    private ListView<String> list;

    private TextArea typingArea;
    @Override
    public void initialize(ConversationPresenter presenter) {
        list = new ListView<>();
        sendButton = new Button();
        sendButton.setText("send message");
        sendButton.setOnAction(e -> {
            String text = typingArea.getText();
            presenter.sendMessage(text);
            typingArea.clear();
        });
        typingArea = new TextArea();
        VBox pane = new VBox(10, list, typingArea, sendButton);
        pane.setPadding(new Insets(10, 10, 10, 10));
        stage = new Stage();
        stage.setTitle(presenter.conversationName());
        Scene conversation = new Scene(pane);
        stage.setScene(conversation);
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
            e.consume();
            presenter.close();
        });
        stage.setEventDispatcher(new ErrorHandler(stage.getEventDispatcher()));
    }

    @Override
    public void displayMessage(String text) {
        list.getItems().add(text);
    }

    @Override
    public void addMessage(String text) {
        list.getItems().add(text);
    }

    @Override
    public void open() {
        stage.show();
    }

    @Override
    public void close() {
        stage.close();
    }
}
