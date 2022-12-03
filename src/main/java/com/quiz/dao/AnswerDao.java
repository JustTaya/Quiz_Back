package com.quiz.dao;

import com.quiz.dao.mapper.AnswerMapper;
import com.quiz.dto.AnswerDto;
import com.quiz.entities.Answer;
import com.quiz.exceptions.DatabaseException;
import com.quiz.service.StoreFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Objects;

import static com.quiz.dao.mapper.AnswerMapper.*;

@Repository
@RequiredArgsConstructor
public class AnswerDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String ANSWER_FIND_BY_ID = "SELECT id, question_id, text, correct, image, next_answer_id FROM answers WHERE id = ?";
    private static final String ANSWER_FIND_BY_QUESTION_ID = "SELECT id, question_id, text, correct, image, next_answer_id FROM answers WHERE question_id = ? ORDER BY id DESC";
    private static final String ANSWER_IMAGE_BY_ID = "SELECT image FROM answers WHERE id = ?";

    private static final String INSERT_ANSWER = "INSERT INTO answers (question_id, text, correct, image, next_answer_id) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_ANSWER = "UPDATE answers SET question_id = ?, text = ?, correct = ?, image=?, next_answer_id = ? WHERE id = ?";
    private static final String UPDATE_ANSWER_IMAGE = "UPDATE answers SET image = ? WHERE id = ?";

    private static final String UPDATE_NEXT_ANSWER_ID = "UPDATE answers SET next_answer_id=? WHERE id=?";

    private static final String DELETE_ANSWER = "DELETE FROM answers WHERE id=?";
    private static final String DELETE_ANSWER_BY_QUESTION_ID = "DELETE FROM answers WHERE question_id = ?";


    public static final String TABLE_ANSWER = "answers";

    public Answer findById(int id) {
        try {
            return jdbcTemplate.queryForObject(ANSWER_FIND_BY_ID, new Object[]{id}, new AnswerMapper());
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find answer by id '%s' database error occured", id));
        }
    }

    public List<Answer> findAnswersByQuestionId(int id) {
        try {
            return jdbcTemplate.query(ANSWER_FIND_BY_QUESTION_ID,
                    new Object[]{id},
                    ((resultSet, i) -> new Answer(resultSet.getInt(ANSWER_ID),
                                resultSet.getInt(ANSWER_QUESTION_ID),
                                resultSet.getString(ANSWER_TEXT),
                                resultSet.getBoolean(ANSWER_CORRECT),
                                resultSet.getInt(ANSWER_NEXT_ANSWER_ID),
                                resultSet.getString("image"))));
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find answer by id '%s' database error occured", id));
        }
    }

    public List<AnswerDto> findAnswerInfoByQuestionId(int id) {
        try {
            return jdbcTemplate.query(ANSWER_FIND_BY_QUESTION_ID,
                    new Object[]{id},
                    (resultSet, i) -> {
                        AnswerDto answerDto = new AnswerDto();

                        answerDto.setId(resultSet.getInt(ANSWER_ID));
                        answerDto.setQuestionId(resultSet.getInt(ANSWER_QUESTION_ID));
                        answerDto.setText(resultSet.getString(ANSWER_TEXT));
                        answerDto.setCorrect(resultSet.getBoolean(ANSWER_CORRECT));
                        answerDto.setImage(resultSet.getString("image"));
                        answerDto.setNextAnswerId(resultSet.getInt(ANSWER_NEXT_ANSWER_ID));

                        return answerDto;
                    }
            );
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find answer by id '%s' database error occured", id));
        }
    }

    public AnswerDto insert(AnswerDto entity, int questionId) {
        entity.setQuestionId(questionId);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(INSERT_ANSWER, new String[]{"id"});
                ps.setInt(1, entity.getQuestionId());
                ps.setString(2, entity.getText());
                ps.setBoolean(3, entity.isCorrect());
                ps.setString(4, entity.getImage());
                if (entity.getNextAnswerId() == null || entity.getNextAnswerId() == 0) {
                    ps.setNull(5, Types.INTEGER);
                } else {
                    ps.setInt(5, entity.getNextAnswerId());
                }

                return ps;
            }, keyHolder);

        } catch (DataAccessException e) {
            throw new DatabaseException("Database access exception while quiz insert");
        }

        entity.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return entity;
    }

    public AnswerDto update(AnswerDto entity, int questionId) {
        if (entity.getId() == null) {
            return insert(entity, questionId);
        } else if (entity.isChanged()) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(UPDATE_ANSWER, new String[]{"id"});
                ps.setInt(1, questionId);
                ps.setString(2, entity.getText());
                ps.setBoolean(3, entity.isCorrect());
                ps.setString(4, entity.getImage());
                if (entity.getNextAnswerId() == null || entity.getNextAnswerId() == 0) {
                    ps.setNull(5, Types.INTEGER);
                } else {
                    ps.setInt(5, entity.getNextAnswerId());
                }
                ps.setInt(6, entity.getId());

                return ps;
            });
        }

        return entity;
    }

    public void delete(AnswerDto entity) {
        if (entity.getId() != null) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(UPDATE_NEXT_ANSWER_ID);
                ps.setNull(1, Types.INTEGER);
                ps.setInt(2, entity.getId());

                return ps;
            });
            jdbcTemplate.update(DELETE_ANSWER, entity.getId());
        }
    }

    public byte[] getAnswerImageByAnswerId(int answerId) {
        List<byte[]> imageBlob = jdbcTemplate.query(
                ANSWER_IMAGE_BY_ID,
                new Object[]{answerId},
                (resultSet, i) -> resultSet.getBytes("images"));

        return imageBlob.get(0);
    }

    public boolean updateAnswerImage(MultipartFile image, int quizId) {
        int affectedRowsNumber = 0;
        try {
            affectedRowsNumber = jdbcTemplate.update(UPDATE_ANSWER_IMAGE, image.getBytes(), quizId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return affectedRowsNumber > 0;
    }

    public void deleteAnswersByQuestionId(int questionId) {
        jdbcTemplate.update(DELETE_ANSWER_BY_QUESTION_ID, questionId);
    }
}
