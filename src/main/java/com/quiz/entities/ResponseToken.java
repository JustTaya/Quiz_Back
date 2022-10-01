package com.quiz.entities;

import lombok.Data;

@Data
public class ResponseToken {
    private String token;

    public ResponseToken(String token) {
        this.token = token;
    }
}
