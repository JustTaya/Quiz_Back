package com.quiz.exceptions;

public class PasswordException extends RuntimeException{

    private static final String  WRONG_PASSWORD = "Wrong Password";
    public PasswordException() {
        super(WRONG_PASSWORD);
    }
}
