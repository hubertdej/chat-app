package com.chat.client.javafxui;

import com.chat.client.domain.application.Dispatcher;
import javafx.application.Platform;

public class GuiDispatcher implements Dispatcher {
    @Override
    public void dispatch(Runnable runnable) {
        Platform.runLater(runnable);
    }
}
