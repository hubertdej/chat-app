package com.chat.client;

import com.chat.client.domain.AccountRepository;
import com.chat.client.javafxui.GuiCallbackDispatcher;
import com.chat.client.presentation.CallbackDispatcher;
import com.chat.client.presentation.PresenterFactory;
import com.chat.client.javafxui.Gui;
import com.chat.client.javafxui.WindowFactory;
import com.chat.client.fake.FakeRestClient;
import com.chat.client.presentation.ViewFactory;

class Program {
    public static void main(String[] args) {
        ViewFactory viewFactory = new WindowFactory();
        AccountRepository accountRepository = new FakeRestClient();
        CallbackDispatcher callbackDispatcher = new GuiCallbackDispatcher();

        Gui.run(() -> new PresenterFactory(viewFactory, accountRepository, callbackDispatcher).openAuthenticationView());
    }
}
