package com.quiz.converters;

import com.quiz.dto.QuizDto;
import com.quiz.entities.Quiz;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class QuizConverter {
    public QuizDto toQuizDto(Quiz quiz) {
        QuizDto quizDto = new QuizDto();

        quizDto.setId(quiz.getId());
        quizDto.setName(quiz.getName());
        quizDto.setAuthor(quiz.getAuthor());
        quizDto.setCategoryId(quiz.getCategoryId());
        quizDto.setDate(quiz.getDate());
        quizDto.setDescription(quiz.getDescription());
        quizDto.setStatus(quiz.getStatus());
        quizDto.setModificationTime(quiz.getModificationTime());
        quizDto.setImage(quiz.getImage());
        quizDto.setCategory(quiz.getCategory());

        quizDto.setQuestions(new ArrayList<>());
        quizDto.setTags(new ArrayList<>());

        quizDto.setChanged(false);

        return quizDto;
    }

    public List<QuizDto> toQuizDtoList(List<Quiz> quizList) {
        List<QuizDto> quizDtoList = new ArrayList<>();

        for (Quiz quiz : quizList) {
            quizDtoList.add(toQuizDto(quiz));
        }

        return quizDtoList;
    }
}
