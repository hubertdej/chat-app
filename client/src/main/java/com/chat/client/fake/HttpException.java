package com.chat.client.fake;

public class HttpException extends RuntimeException {
    HttpException(int statusCode) { super("Unwanted status code:" + statusCode); }
}
