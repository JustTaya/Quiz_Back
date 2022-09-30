package com.quiz.exceptions;

public class EmailExistException extends RuntimeException{
    public EmailExistException(String message) {
        super(message);
    }
}
