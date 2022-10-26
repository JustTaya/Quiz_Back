package com.quiz.dao;

import com.quiz.dao.mapper.QuizMapper;
import com.quiz.dto.QuizDto;
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

@Repository
@RequiredArgsConstructor
public class QuizDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String GET_QUIZZES_BY_STATUS = "SELECT * FROM quizzes WHERE status = ?::status_type";
    private static final String GET_ALL_QUIZZES = "SELECT * FROM quizzes";
    private static final String GET_QUIZ_BY_ID = "SELECT * FROM quizzes WHERE id = ?";
    private static final String GET_QUIZZES_CREATED_BY_USER_ID = "SELECT * FROM quizzes WHERE author = ? AND (status<>'DELETED' AND status<>'DEACTIVATED')";
    private static final String GET_FAVORITE_QUIZZES_BY_USER_ID = "SELECT * FROM quizzes INNER JOIN favorite_quizzes ON id = quiz_id WHERE user_id = ?";
    private static final String GET_QUIZZES_BY_CATEGORY_ID = "SELECT * FROM quizzes WHERE category_id = ?";
    private static final String GET_QUIZZES_BY_TAG = "SELECT * FROM quizzes INNER JOIN quizzes_tags on id = quiz_id where tag_id = ?";
    private static final String GET_QUIZZES_BY_NAME = "SELECT * FROM quizzes WHERE name LIKE ?";
    private static final String INSERT_QUIZ = "INSERT INTO quizzes (name , author, category_id, date, description, status, modification_time) VALUES (?,?,?,?,?,CAST(? AS status_type),?)";
    private static final String UPDATE_QUIZ = "UPDATE quizzes SET name = ?, author = ?, category_id = ?, date = ?, description = ?, status = ?::status_type, modification_time = ? WHERE id = ?";
    public static final String TABLE_QUIZZES = "quizzes";
    private static final String GET_GAMES_CREATED_BY_USER_ID = "SELECT * FROM quizzes WHERE author = ?";
    private static final String GET_FAVORITE_GAMES_BY_USER_ID = "SELECT * FROM quizzes INNER JOIN favorite_quizzes ON id = quiz_id WHERE user_id = ?";
    private static final String GET_QUIZ_CATEGORY_BY_CATEGORY_ID = "SELECT name FROM categories WHERE id = ?";
    private static final String GET_QUIZZES_BY_STATUS_NAME = "SELECT sq.qid qid,sq.qauthor qauthor, u.id uid, u.name uname, u.surname usurname, u.email uemail, sq.qdate qdate,sq.qdescription qdescription,sq.qimage qimage, sq.qmodificationtime qmodificationtime, sq.qname qname, sq.cname cname, sq.qcategoryid qcategoryid, sq.qstatus qstatus FROM (SELECT q.id qid,q.author qauthor,q.date qdate,q.description qdescription,q.image qimage, q.modification_time qmodificationtime, q.name qname, q.category_id qcategoryid, q.status qstatus, c.name cname FROM quizzes q INNER JOIN categories c on q.category_id = c.id where q.status = ?::status_type) sq INNER JOIN users u on sq.qauthor = u.id";
    private static final String GET_QUIZ_BY_ID_NAME = "SELECT sq.qid qid,sq.qauthor qauthor, u.id uid, u.name uname, u.surname usurname, u.email uemail, sq.qdate qdate,sq.qdescription qdescription,sq.qimage qimage, sq.qmodificationtime qmodificationtime, sq.qname qname, sq.cname cname, sq.qcategoryid qcategoryid, sq.qstatus qstatus FROM (SELECT q.id qid,q.author qauthor,q.date qdate,q.description qdescription,q.image qimage, q.modification_time qmodificationtime, q.name qname, q.category_id qcategoryid, q.status qstatus, c.name cname FROM quizzes q INNER JOIN categories c on q.category_id = c.id where q.id = ?) sq INNER JOIN users u on sq.qauthor = u.id";


    public List<Quiz> getGamesCreatedByUser(int userId) {

        List<Quiz> quizzesCreatedByUser = jdbcTemplate.query(GET_GAMES_CREATED_BY_USER_ID, new Object[]{userId}, new QuizMapper());

        if (quizzesCreatedByUser.isEmpty()){
            return null;
        }

        return quizzesCreatedByUser;
    }

    public List<QuizDto> getQuizzesByStatus(StatusType status) {

        List<QuizDto> quizDtos = jdbcTemplate.query(
                GET_QUIZZES_BY_STATUS_NAME,
                new Object[]{status.toString()}, (resultSet, i) -> {
                    QuizDto quiz = new QuizDto();
                    quiz.setName(resultSet.getString("qname"));
                    quiz.setCategoryId(resultSet.getInt("qcategoryid"));
                    quiz.setStatus(StatusType.valueOf(resultSet.getString("qstatus")));
                    quiz.setCategory(resultSet.getString("cname"));
                    quiz.setId(resultSet.getInt("qid"));
                    quiz.setAuthor(resultSet.getInt("qauthor"));
                    quiz.setAuthorName(resultSet.getString("uname"));
                    quiz.setAuthorSurname(resultSet.getString("usurname"));
                    quiz.setAuthorEmail(resultSet.getString("uemail"));
                    quiz.setDate(resultSet.getDate("qdate"));
                    quiz.setDescription(resultSet.getString("qdescription"));
                    quiz.setModificationTime(resultSet.getTimestamp("qmodificationtime"));

                    return quiz;
                });

        if (quizDtos.isEmpty()) {
            return null;
        }

        return quizDtos;
    }

    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = jdbcTemplate.query(GET_ALL_QUIZZES, new QuizMapper());

        if (quizzes.isEmpty()) {
            return null;
        }

        return quizzes;
    }

    public QuizDto findById(int id) {
        List<QuizDto> quizzes;

        try {
            quizzes = jdbcTemplate.query(
                    GET_QUIZ_BY_ID_NAME,
                    new Object[]{id}, (resultSet, i) -> {
                        QuizDto quiz = new QuizDto();
                        quiz.setName(resultSet.getString("qname"));
                        quiz.setCategoryId(resultSet.getInt("qcategoryid"));
                        quiz.setStatus(StatusType.valueOf(resultSet.getString("qstatus")));
                        quiz.setCategory(resultSet.getString("cname"));
                        quiz.setId(resultSet.getInt("qid"));
                        quiz.setAuthor(resultSet.getInt("qauthor"));
                        quiz.setAuthorName(resultSet.getString("uname"));
                        quiz.setAuthorSurname(resultSet.getString("usurname"));
                        quiz.setAuthorEmail(resultSet.getString("uemail"));
                        quiz.setDate(resultSet.getDate("qdate"));
                        quiz.setDescription(resultSet.getString("qdescription"));
                        quiz.setModificationTime(resultSet.getTimestamp("qmodificationtime"));

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

    public String getCategoryNameByCategoryId(int categoryId){
        List<String> categoryNames = jdbcTemplate.query(GET_QUIZ_CATEGORY_BY_CATEGORY_ID, new Object[]{categoryId}, (resultSet, i) -> resultSet.getString("name"));

        return categoryNames.get(0);
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
                ps.setInt(3, entity.getCategoryId());
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
                quiz.getAuthor(), quiz.getCategoryId(),
                quiz.getDate(), quiz.getDescription(),
                quiz.getStatus(), quiz.getModificationTime(), quiz.getId());

        return affectedRowNumber > 0;
    }
}
