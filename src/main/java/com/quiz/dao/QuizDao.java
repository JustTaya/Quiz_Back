package com.quiz.dao;

import com.quiz.dao.mapper.QuizMapper;
import com.quiz.entities.Quiz;
import com.quiz.entities.StatusType;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.quiz.dao.mapper.QuizMapper.*;

@Repository
@RequiredArgsConstructor
public class QuizDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String GET_QUIZZES_BY_STATUS = "SELECT * FROM quizzes WHERE status = ?";
    private static final String GET_ALL_QUIZZES = "SELECT * FROM quizzes";
    private static final String GET_QUIZ_BY_ID = "SELECT * FROM quizzes WHERE id = ?";
    private static final String GET_QUIZZES_CREATED_BY_USER_ID = "SELECT * FROM quizzes WHERE author = ?";
    private static final String GET_FAVORITE_QUIZZES_BY_USER_ID = "SELECT * FROM quizzes INNER JOIN favorite_quizzes ON id = quiz_id WHERE user_id = ?";
    private static final String GET_QUIZZES_BY_CATEGORY_ID = "SELECT * FROM quizzes WHERE category_id = ?";
    private static final String GET_QUIZZES_BY_TAG = "SELECT * FROM quizzes INNER JOIN quizzes_tags on id = quiz_id where tag_id = ?";
    private static final String GET_QUIZZES_BY_NAME = "SELECT * FROM quizzes WHERE name LIKE ?";
    private static final String INSERT_QUIZ = "INSERT INTO quizzes (id, name, author, category_id, date, description, status, modification_time) VALUES (?,?,?,?,?,?,?,?)";
    private static final String UPDATE_QUIZ = "UPDATE quizzes SET name = ?, author = ?, category_id = ?, date = ?, description = ?, status = ?, modification_time = ? WHERE id = ?";
    public static final String TABLE_QUIZZES = "quizzes";

    public List<Quiz> getQuizzesByStatus(StatusType status) {

        List<Quiz> quizzesByStatus = jdbcTemplate.query(GET_QUIZZES_BY_STATUS, new Object[]{status}, new QuizMapper());

        if (quizzesByStatus.isEmpty()){
            return null;
        }

        return quizzesByStatus;
    }

    public List<Quiz> getAllQuizzes(){
        List<Quiz> quizzes = jdbcTemplate.query(GET_ALL_QUIZZES, new QuizMapper());

        if (quizzes.isEmpty()){
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

        if (quizzesCreatedByUser.isEmpty()){
            return null;
        }

        return quizzesCreatedByUser;
    }

    public List<Quiz> findQuizzesByName(String name) {

        List<Quiz> quizzesByName = jdbcTemplate.query(GET_QUIZZES_BY_NAME, new Object[]{"%"+name+"%"}, new QuizMapper());

        if (quizzesByName.isEmpty()){
            return null;
        }

        return quizzesByName;
    }

    public List<Quiz> getFavoriteQuizzesByUserId(int userId) {
        List<Quiz> quizzesFavoriteByUser = jdbcTemplate.query(GET_FAVORITE_QUIZZES_BY_USER_ID, new Object[]{userId}, new QuizMapper());

        if (quizzesFavoriteByUser.isEmpty()){
            return null;
        }

        return quizzesFavoriteByUser;
    }

    public List<Quiz> getQuizzesByCategory(int categoryId) {

        List<Quiz> quizzesByCategory = jdbcTemplate.query(GET_QUIZZES_BY_CATEGORY_ID, new Object[]{categoryId}, new QuizMapper());

        if (quizzesByCategory.isEmpty()){
            return null;
        }

        return quizzesByCategory;
    }

    public List<Quiz> getQuizzesByTag(int tagId) {

        List<Quiz> quizzesByTag = jdbcTemplate.query(GET_QUIZZES_BY_TAG, new Object[]{tagId}, new QuizMapper());

        if (quizzesByTag.isEmpty()){
            return null;
        }

        return quizzesByTag;
    }

    @Transactional
    public Quiz insert(Quiz entity) {
        int id;

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName(TABLE_QUIZZES)
                .usingGeneratedKeyColumns(QuizMapper.QUIZ_ID);


        Map<String, Object> parameters = new HashMap<>();
        parameters.put(QuizMapper.QUIZ_ID, entity.getId());
        parameters.put(QuizMapper.QUIZ_NAME, entity.getName());
        parameters.put(QuizMapper.QUIZ_AUTHOR, entity.getAuthor());
        parameters.put(QuizMapper.QUIZ_CATEGORY, entity.getCategory_id());
        parameters.put(QuizMapper.QUIZ_DATE, entity.getDate());
        parameters.put(QuizMapper.QUIZ_DESCRIPTION, entity.getDescription());
        parameters.put(QuizMapper.QUIZ_STATUS, entity.getStatus());
        parameters.put(QuizMapper.QUIZ_MODIFICATION_TIME, entity.getModificationTime());


        try {
            jdbcTemplate.update(INSERT_QUIZ, entity.getName(), entity.getAuthor(), entity.getCategory_id(), entity.getDate(), entity.getDescription(), entity.getStatus(), entity.getModificationTime());
            //entity.setId(id);
        } catch (DataAccessException e) {
            throw new DatabaseException("Database access exception while quiz insert");
        }

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
