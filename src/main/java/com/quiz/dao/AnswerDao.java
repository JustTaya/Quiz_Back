package com.quiz.dao;

import com.quiz.dao.mapper.AnswerMapper;
import com.quiz.entities.Answer;
import com.quiz.exceptions.DatabaseException;
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
import java.util.List;
import java.util.Objects;

import static com.quiz.dao.mapper.AnswerMapper.*;

@Repository
@RequiredArgsConstructor
public class AnswerDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String ANSWER_FIND_BY_ID = "SELECT id, question_id, text, correct, next_answer_id FROM answers WHERE id = ?";
    private static final String ANSWER_FIND_BY_QUESTION_ID = "SELECT id, question_id, text, correct, next_answer_id FROM answers WHERE question_id = ?";
    private static final String ANSWER_IMAGE_BY_ID = "SELECT image FROM answers WHERE id = ?";

    private static final String INSERT_ANSWER = "INSERT INTO answers (question_id, text, correct) VALUES (?, ?, ?)";

    private static final String UPDATE_ANSWER = "UPDATE answers SET question_id = ?, text = ?, correct = ?, next_answer_id = ? WHERE id = ?";
    private static final String UPDATE_ANSWER_IMAGE = "UPDATE answers SET image = ? WHERE id = ?";

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
            return jdbcTemplate.query(ANSWER_FIND_BY_QUESTION_ID, new Object[]{id}, new AnswerMapper());
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find answer by id '%s' database error occured", id));
        }
    }

    @Transactional
    public Answer insert(Answer answer) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(INSERT_ANSWER, new String[]{"id"});
                ps.setInt(1, answer.getQuestionId());
                ps.setString(2, answer.getText());
                ps.setBoolean(3, answer.isCorrect());

                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            throw new DatabaseException("Database access exception while quiz insert");
        }

        answer.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return answer;
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

    public boolean updateAnswer(Answer answer) {
        int affectedRowNumber = jdbcTemplate.update(UPDATE_ANSWER,
                answer.getQuestionId(),
                answer.getText(),
                answer.isCorrect(),
                answer.getNextAnswerId(),
                answer.getId()
        );

        return affectedRowNumber > 0;
    }
}
