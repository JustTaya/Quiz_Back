package com.quiz.dto;

import com.quiz.entities.Question;
import com.quiz.entities.QuestionType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuestionDto {
    private int id;
    private int quizId;
    private QuestionType type;
    private String text;
    private boolean active;
    private int languageId;

    public QuestionDto(Question question){
        this.id=question.getId();
        this.quizId=question.getQuizId();
        this.type=question.getType();
        this.text=question.getText();
        this.active=question.isActive();
        this.languageId=question.getLanguageId();
    }
}
