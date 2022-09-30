package com.quiz.exceptions;

public class NotFoundException extends RuntimeException {

    private static final String NOT_FOUND_BY_PARAM = "Object %s not found by %s '%s'";

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String object, String param, String value) {
        super(String.format(NOT_FOUND_BY_PARAM, object, param, value));
    }
}
