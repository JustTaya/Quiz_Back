package com.quiz.service;

import com.quiz.dao.AnswerDao;
import com.quiz.dto.AnswerDto;
import com.quiz.entities.Answer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerDao answerDao;

    public Answer findById(int id) {
        return answerDao.findById(id);
    }

    public List<Answer> findAnswersByQuestionId(int id) {
        return answerDao.findAnswersByQuestionId(id);
    }

    public AnswerDto insertAnswer(Answer answer) {
        answerDao.insert(answer);
        return new AnswerDto(answer);
    }

    public boolean updateAnswer(Answer answer) {
        return answerDao.updateAnswer(answer);
    }
}
