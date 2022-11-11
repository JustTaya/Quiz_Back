package com.quiz.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class Question {
    private int id;
    private int quizId;
    private QuestionType type;
    private String text;
    private boolean active;
    private int languageId;
    private List<Answer> answerList;
}
