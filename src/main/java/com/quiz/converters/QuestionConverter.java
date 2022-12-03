package com.quiz.converters;

import com.quiz.dto.QuestionDto;
import com.quiz.entities.Question;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuestionConverter {
    public QuestionDto toQuestionDto(Question question) {
        QuestionDto questionDto = new QuestionDto();

        questionDto.setId(question.getId());
        questionDto.setQuizId(question.getQuizId());
        questionDto.setText(question.getText());
        questionDto.setActive(true);
        questionDto.setLanguageId(question.getLanguageId());
        questionDto.setAnswerList(new ArrayList<>());
        questionDto.setChanged(false);
        questionDto.setDeleted(false);

        return questionDto;
    }
}
