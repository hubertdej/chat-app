package com.chat.client;

import com.chat.client.logic.PresenterFactory;
import com.chat.client.model.Preview;
import com.chat.client.ui.Gui;
import com.chat.client.ui.WindowFactory;

class Program {
    public static void main(String[] args) {
        Gui.run(() -> new PresenterFactory(new WindowFactory()).openAuthenticationView());
    }
}
