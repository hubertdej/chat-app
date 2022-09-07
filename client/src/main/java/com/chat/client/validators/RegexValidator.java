package com.chat.client.validators;

import java.util.regex.Pattern;

public class RegexValidator implements Validator<String> {
    private final Pattern pattern;
    private final String errorMessage;

    public RegexValidator(String regex, String errorMessage) {
        this.pattern = Pattern.compile(regex);
        this.errorMessage = errorMessage;
    }

    @Override
    public void validate(String text) {
        var matcher = pattern.matcher(text);
        if (!matcher.matches()) {
            throw new ValidationException(errorMessage);
        }
    }
}
