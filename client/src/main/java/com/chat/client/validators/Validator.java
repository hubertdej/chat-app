package com.chat.client.validators;

public interface Validator<T>{
    void validate(T t) throws ValidationException;
}
