package com.quiz.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseToken {
    private String token;

    private String id;
    private String email;
    private String role;
}
