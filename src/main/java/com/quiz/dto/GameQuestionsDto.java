package com.quiz.dto;

import com.quiz.entities.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameQuestionsDto {
    private int questionNumber;
    private int questionTimer;
    private Question question;
}
