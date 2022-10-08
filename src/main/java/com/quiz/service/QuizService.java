package com.quiz.service;

import com.quiz.dao.QuizDao;
import com.quiz.dto.QuizDto;
import com.quiz.entities.Quiz;
import com.quiz.entities.StatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuizService {

    private final QuizDao quizDao;

    public List<Quiz> findQuizzesByStatus(StatusType status){
        return quizDao.getQuizzesByStatus(status);
    }

    public List<Quiz> findAllQuizzes(){
        return quizDao.getAllQuizzes();
    }

    public Quiz findQuizById(int id) {
        return quizDao.findById(id);
    }

    public List<Quiz> findQuizzesCreatedByUserId(int userId) {
        return quizDao.getQuizzesCreatedByUser(userId);
    }

    public List<Quiz> findFavoriteQuizzes(int userId) {
        return quizDao.getFavoriteQuizzesByUserId(userId);
    }

    public List<Quiz> findQuizzesByCategory(int categoryId) {
        return quizDao.getQuizzesByCategory(categoryId);
    }

    public List<Quiz> findQuizzesByTag(int tagId) {
        return quizDao.getQuizzesByTag(tagId);
    }

    public List<Quiz> findQuizzesByName(String name) {
        return quizDao.findQuizzesByName(name);
    }

    public boolean updateQuiz(Quiz quiz) {
        return quizDao.updateQuiz(quiz);
    }

    public QuizDto insertQuiz(Quiz quiz) {
        quizDao.insert(quiz);
        return new QuizDto(quiz);
    }

}
