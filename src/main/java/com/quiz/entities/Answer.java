package com.quiz.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Answer {

    private int id;
    private int questionId;
    private String text;
    private boolean correct;
    private int nextAnswerId;
}