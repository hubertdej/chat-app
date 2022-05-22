package com.chat.client.model;

import java.util.regex.Pattern;

public class EmailValidator {
    private final Pattern pattern = Pattern.compile("^[a-z0-9._%-]+@[a-z0-9._%-]+\\.[a-z]{2,4}$");

    public Boolean isValid(String text) {
        return pattern.matcher(text).matches();
    }
}
