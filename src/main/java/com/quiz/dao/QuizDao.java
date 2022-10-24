package com.quiz.dao;

import com.quiz.dao.mapper.QuizMapper;
import com.quiz.entities.Quiz;
import com.quiz.entities.StatusType;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

import static com.quiz.dao.mapper.QuizMapper.*;

@Repository
@RequiredArgsConstructor
public class QuizDao {

    private final JdbcTemplate jdbcTemplate;

    private final static String GET_QUIZZES_BY_STATUS = "SELECT * FROM quizzes WHERE status = ?::status_type";
    private final static String GET_ALL_QUIZZES = "SELECT * FROM quizzes";
    private final static String GET_QUIZ_BY_ID = "SELECT * FROM quizzes WHERE id = ?";
    private final static String GET_QUIZZES_CREATED_BY_USER_ID = "SELECT * FROM quizzes WHERE author = ?";
    private final static String GET_FAVORITE_QUIZZES_BY_USER_ID = "SELECT * FROM quizzes INNER JOIN favorite_quizzes ON id = quiz_id WHERE user_id = ?";
    private final static String GET_QUIZZES_BY_CATEGORY_ID = "SELECT * FROM quizzes WHERE category_id = ?";
    private final static String GET_QUIZZES_BY_TAG = "SELECT * FROM quizzes INNER JOIN quizzes_tags on id = quiz_id where tag_id = ?";
    private final static String GET_QUIZZES_BY_NAME = "SELECT * FROM quizzes WHERE name LIKE ?";
    private final static String INSERT_QUIZ = "INSERT INTO quizzes (name , author, category_id, date, description,status, modification_time) VALUES (?,?,?,?,?,?::status_type,?)";
    private final static String UPDATE_QUIZ = "UPDATE quizzes SET name = ?, author = ?, category_id = ?, date = ?, description = ?, status = ?::status_type, modification_time = ? WHERE id = ?";
    public static final String TABLE_QUIZZES = "quizzes";

    public List<Quiz> getQuizzesByStatus(StatusType status) {

        List<Quiz> quizzesByStatus = jdbcTemplate.query(GET_QUIZZES_BY_STATUS, new Object[]{status}, new QuizMapper());

        if (quizzesByStatus.isEmpty()) {
            return null;
        }

        return quizzesByStatus;
    }

    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = jdbcTemplate.query(GET_ALL_QUIZZES, new QuizMapper());

        if (quizzes.isEmpty()) {
            return null;
        }

        return quizzes;
    }

    public Quiz findById(int id) {
        List<Quiz> quizzes;

        try {
            quizzes = jdbcTemplate.query(
                    GET_QUIZ_BY_ID,
                    new Object[]{id}, (resultSet, i) -> {
                        Quiz quiz = new Quiz();

                        quiz.setId(resultSet.getInt(QUIZ_ID));
                        quiz.setName(resultSet.getString(QUIZ_NAME));
                        quiz.setAuthor(resultSet.getInt(QUIZ_AUTHOR));
                        quiz.setCategory_id(resultSet.getInt(QUIZ_CATEGORY));
                        quiz.setDate(resultSet.getDate(QUIZ_DATE));
                        quiz.setDescription(resultSet.getString(QUIZ_DESCRIPTION));
                        quiz.setStatus(StatusType.valueOf(resultSet.getString(QUIZ_STATUS)));
                        quiz.setModificationTime(resultSet.getTimestamp(QUIZ_MODIFICATION_TIME));
                        return quiz;
                    }
            );
            if (quizzes.isEmpty()) {
                return null;
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find quiz by id '%s' database error occured", id));
        }

        return quizzes.get(0);
    }

    public List<Quiz> getQuizzesCreatedByUser(int userId) {

        List<Quiz> quizzesCreatedByUser = jdbcTemplate.query(GET_QUIZZES_CREATED_BY_USER_ID, new Object[]{userId}, new QuizMapper());

        if (quizzesCreatedByUser.isEmpty()) {
            return null;
        }

        return quizzesCreatedByUser;
    }

    public List<Quiz> findQuizzesByName(String name) {

        List<Quiz> quizzesByName = jdbcTemplate.query(GET_QUIZZES_BY_NAME, new Object[]{"%" + name + "%"}, new QuizMapper());

        if (quizzesByName.isEmpty()) {
            return null;
        }

        return quizzesByName;
    }

    public List<Quiz> getFavoriteQuizzesByUserId(int userId) {
        List<Quiz> quizzesFavoriteByUser = jdbcTemplate.query(GET_FAVORITE_QUIZZES_BY_USER_ID, new Object[]{userId}, new QuizMapper());

        if (quizzesFavoriteByUser.isEmpty()) {
            return null;
        }

        return quizzesFavoriteByUser;
    }

    public List<Quiz> getQuizzesByCategory(int categoryId) {

        List<Quiz> quizzesByCategory = jdbcTemplate.query(GET_QUIZZES_BY_CATEGORY_ID, new Object[]{categoryId}, new QuizMapper());

        if (quizzesByCategory.isEmpty()) {
            return null;
        }

        return quizzesByCategory;
    }

    public List<Quiz> getQuizzesByTag(int tagId) {

        List<Quiz> quizzesByTag = jdbcTemplate.query(GET_QUIZZES_BY_TAG, new Object[]{tagId}, new QuizMapper());

        if (quizzesByTag.isEmpty()) {
            return null;
        }

        return quizzesByTag;
    }

    @Transactional
    public Quiz insert(Quiz entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(INSERT_QUIZ, new String[]{"id"});
                ps.setString(1, entity.getName());
                ps.setInt(2, entity.getAuthor());
                ps.setInt(3, entity.getCategory_id());
                ps.setDate(4, entity.getDate());
                ps.setString(5, entity.getDescription());
                ps.setString(6, String.valueOf(entity.getStatus()));
                ps.setTimestamp(7, entity.getModificationTime());
                return ps;
            }, keyHolder);

        } catch (DataAccessException e) {
            throw new DatabaseException("Database access exception while quiz insert");
        }

        entity.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return entity;
    }

    public boolean updateQuiz(Quiz quiz) {
        int affectedRowNumber = jdbcTemplate.update(UPDATE_QUIZ, quiz.getName(),
                quiz.getAuthor(), quiz.getCategory_id(),
                quiz.getDate(), quiz.getDescription(),
                quiz.getStatus(), quiz.getModificationTime(), quiz.getId());

        return affectedRowNumber > 0;
    }
}
