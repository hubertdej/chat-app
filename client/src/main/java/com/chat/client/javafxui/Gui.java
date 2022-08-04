package com.chat.client.javafxui;

import javafx.application.Platform;

public class Gui {
    public static void run(Runnable runnable) { Platform.startup(runnable); }
}
