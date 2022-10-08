package com.quiz.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Question {
    private int id;
    private int quizId;
    private QuestionType type;
    private String text;
    private boolean active;
    private int languageId;
}
