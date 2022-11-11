package com.quiz.service;

import com.quiz.dao.QuizDao;
import com.quiz.dto.QuizDto;
import com.quiz.entities.Quiz;
import com.quiz.entities.StatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuizService {

    private final QuizDao quizDao;

    public List<QuizDto> findQuizzesByStatus(StatusType status) {
        return quizDao.getQuizzesByStatus(status);
    }

    public List<Quiz> findAllQuizzes(int userId) {
        return quizDao.getAllQuizzes(userId);
    }

    public QuizDto findQuizById(int id) {
        return quizDao.findById(id);
    }

    public List<Quiz> findQuizzesCreatedByUserId(int userId, String sort) {
        return quizDao.getQuizzesCreatedByUser(userId, sort);
    }

    public List<Quiz> findFavoriteQuizzes(int userId) {
        return quizDao.getFavoriteQuizzesByUserId(userId);
    }

    public List<Quiz> findQuizzesByCategory(int categoryId, int userId) {
        return quizDao.getQuizzesByCategory(categoryId, userId);
    }

    public List<Quiz> findQuizzesByTag(int tagId) {
        return quizDao.getQuizzesByTag(tagId);
    }

    public List<Quiz> findQuizzesByName(String name) {
        return quizDao.findQuizzesByName(name);
    }

    public byte[] getImageByQuizId(int quizId) {
        return quizDao.getQuizImageByQuizId(quizId);
    }

    public boolean updateQuiz(Quiz quiz) {
        return quizDao.updateQuiz(quiz);
    }

    public boolean updateQuizImage(MultipartFile image, int quizId) {
        return quizDao.updateQuizImage(image, quizId);
    }

    public QuizDto insertQuiz(Quiz quiz) {
        quizDao.insert(quiz);
        return new QuizDto(quiz);
    }


    public String getCategoryNameByCategoryId(int categoryId){
        return quizDao.getCategoryNameByCategoryId(categoryId);
    }
    public boolean addTag(int quizId, int tagId) {
        return quizDao.addTagToQuiz(quizId, tagId);
    }

    public List<Quiz> findTopPopularQuizzes(int limit) {
        return quizDao.getTopPopularQuizzes(limit);
    }

    public List<Quiz> findTopPopularQuizzesByCategory(int categoryId, int limit) {
        return quizDao.getTopPopularQuizzesByCategory(categoryId, limit);
    }

    public List<Quiz> findRecentGames(int userId, int limit) {
        return quizDao.getRecentGames(userId, limit);
    }

    public List<Quiz> getQuizzesByFilter(String searchByUser, int userId) {
        return quizDao.getQuizzesByFilter(searchByUser, userId);
    }

    public boolean markQuizAsFavorite(int quizId, int userId) {
        return quizDao.markQuizAsFavorite(quizId, userId);
    }

    public boolean unmarkQuizAsFavorite(int quizId, int userId) {
        return quizDao.unmarkQuizAsFavorite(quizId, userId);
    }
    public List<Quiz> findRecommendations(int userId, int limit){
        return quizDao.getRecommendations(userId,limit);
    }

    public List<Quiz> findRecommendationsByFriends(int userId, int limit){
        return quizDao.getRecommendationsByFriends(userId,limit);
    }

    public List<Quiz> findPopularQuizzes(int limit) {
        return  quizDao.getPopularQuizzes(limit);
    }

    public List<Quiz> filterQuizzesByUserId(String userSearch, int userId, String sort) {
        return quizDao.filterQuizzesByUserId(userSearch, userId, sort);
    }

    public List<Quiz> searchInFavoriteQuizzes(int userId, String userSearch) {
        return quizDao.searchInFavoriteQuizzes(userId, userSearch);
    }

}
