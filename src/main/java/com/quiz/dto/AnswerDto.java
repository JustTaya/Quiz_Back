package com.quiz.dto;

import com.quiz.entities.Answer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    private int id;
    private int questionId;
    private String text;
    private boolean correct;
    private int nextAnswerId;

    public AnswerDto(Answer answer) {
        this.id = answer.getId();
        this.questionId = answer.getQuestionId();
        this.text = answer.getText();
        this.correct = answer.isCorrect();
        this.nextAnswerId = answer.getNextAnswerId();
    }
}
