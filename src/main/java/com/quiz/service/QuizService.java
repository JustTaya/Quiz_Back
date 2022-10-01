package com.quiz.service;

import com.quiz.dao.QuizDao;
import com.quiz.entities.Quiz;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuizService {

    private final QuizDao quizDao;

    public List<Quiz> findQuizzesCreatedByUserId(int userId) {
        return quizDao.getGamesCreatedByUser(userId);
    }

    public List<Quiz> findFavoriteQuizzes(int userId) {
        return quizDao.getFavoriteGamesByUserId(userId);
    }
}
