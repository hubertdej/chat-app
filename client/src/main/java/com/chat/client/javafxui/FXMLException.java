package com.chat.client.javafxui;

import java.io.IOException;

public class FXMLException extends RuntimeException {
    FXMLException(IOException cause) { super(cause); }
}
