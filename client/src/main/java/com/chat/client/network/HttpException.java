package com.chat.client.network;

public class HttpException extends RuntimeException {
    HttpException(int statusCode) { super("Unwanted status code:" + statusCode); }
}
