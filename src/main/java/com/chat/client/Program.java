package com.chat.client;

import com.chat.client.domain.application.AuthService;
import com.chat.client.javafxui.GuiCallbackDispatcher;
import com.chat.client.domain.application.CallbackDispatcher;
import com.chat.client.presentation.PresenterFactory;
import com.chat.client.javafxui.Gui;
import com.chat.client.javafxui.WindowFactory;
import com.chat.client.fake.FakeAuthService;
import com.chat.client.presentation.ViewFactory;

class Program {
    public static void main(String[] args) {
        ViewFactory viewFactory = new WindowFactory();
        AuthService authService = new FakeAuthService();
        CallbackDispatcher callbackDispatcher = new GuiCallbackDispatcher();

        Gui.run(() -> new PresenterFactory(viewFactory, authService, callbackDispatcher).openAuthView());
    }
}
