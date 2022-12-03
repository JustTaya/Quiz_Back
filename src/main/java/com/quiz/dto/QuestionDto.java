package com.quiz.dto;

import com.quiz.entities.QuestionType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class QuestionDto {
    private Integer id;
    private int quizId;
    private QuestionType type;
    private String image;
    private String text;
    private boolean active;
    private int languageId;
    private List<AnswerDto> answerList;
    private boolean changed;
    private boolean changedType;
    private boolean deleted;
}
