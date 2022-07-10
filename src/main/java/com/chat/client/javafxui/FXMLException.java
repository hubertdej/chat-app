package com.chat.client.javafxui;

import java.io.IOException;

class FXMLException extends RuntimeException {
    FXMLException(IOException cause) { super(cause); }
}
