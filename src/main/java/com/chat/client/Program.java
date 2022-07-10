package com.chat.client;

import com.chat.client.presentation.PresenterFactory;
import com.chat.client.javafxui.Gui;
import com.chat.client.javafxui.WindowFactory;

class Program {
    public static void main(String[] args) {
        Gui.run(() -> new PresenterFactory(new WindowFactory()).openAuthenticationView());
    }
}
