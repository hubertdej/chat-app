package com.chat.client.ui;

import com.chat.client.logic.LoggedView;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import com.chat.client.logic.PreviewPresenter;

public class LoggedWindow implements LoggedView {
    private Stage stage;
    private ListView<String> list;
    private Button tempButton;
    @Override
    public void addConversation(String name) {
        list.getItems().add(name);
    }
    @Override
    public void initialize(PreviewPresenter presenter) {
        list = new ListView<>();
        tempButton = new Button();
        tempButton.setText("go to conversation");
        tempButton.setOnAction(e -> {
            int index = list.getSelectionModel().getSelectedIndex();
            if (index >= 0) presenter.openConversationScene(index);
        });

        VBox pane = new VBox(10, list, tempButton);
        pane.setPadding(new Insets(10, 10, 10, 10));
        stage = new Stage();
        stage.setTitle("Logged in");
        Scene logged = new Scene(pane);
        stage.setScene(logged);
        stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, e -> {
            e.consume();
            presenter.close();
        });
        stage.setEventDispatcher(new ErrorHandler(stage.getEventDispatcher()));
    }

    public void open() { stage.show(); }
    public void close() { stage.close(); }
}
