package com.quiz.dao;

import com.quiz.entities.Question;
import com.quiz.entities.QuestionType;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.quiz.dao.mapper.QuestionMapper.*;

@Repository
@RequiredArgsConstructor
public class QuestionDao {
    private final JdbcTemplate jdbcTemplate;

    private final static String QUESTION_FIND_BY_ID = "SELECT id, quiz_id, type, text, active FROM questions WHERE id = ?";
    private final static String QUESTION_FIND_BY_QUIZ_ID = "SELECT id, quiz_id, type, text, active FROM questions WHERE quiz_id = ?";

    private final static String INSERT_QUESTION = "INSERT INTO questions (quiz_id, type, text, active) VALUES ( ?, ?::question_type, ?, ?)";

    private final static String UPDATE_QUESTION = "UPDATE questions SET type=?, text=?, active=? WHERE id=?";

    public static final String TABLE_QUESTIONS = "questions";

    public Question findById(int id) {
        List<Question> questions;

        try {
            questions = jdbcTemplate.query(
                    QUESTION_FIND_BY_ID,
                    new Object[]{id},
                    (resultSet, i) -> {
                        Question question = new Question();

                        question.setId(resultSet.getInt(QUESTION_ID));
                        question.setQuizId(resultSet.getInt(QUESTION_QUIZ_ID));
                        question.setType(QuestionType.valueOf(resultSet.getString(QUESTION_TYPE)));
                        question.setText(resultSet.getString(QUESTION_TEXT));
                        question.setActive(resultSet.getBoolean(QUESTION_ACTIVE));

                        return question;
                    }
            );
            if (questions.isEmpty()) {
                return null;
            }

        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find question by id '%s' database error occured", id));
        }

        return questions.get(0);
    }

    public List<Question> findQuestionsByQuizId(int id) {
        return jdbcTemplate.query(
                QUESTION_FIND_BY_QUIZ_ID,
                new Object[]{id},
                (resultSet, i) -> {
                    Question question = new Question();

                    question.setId(resultSet.getInt(QUESTION_ID));
                    question.setQuizId(resultSet.getInt(QUESTION_QUIZ_ID));
                    question.setType(QuestionType.valueOf(resultSet.getString(QUESTION_TYPE)));
                    question.setText(resultSet.getString(QUESTION_TEXT));
                    question.setActive(resultSet.getBoolean(QUESTION_ACTIVE));

                    return question;
                });
    }

    @Transactional
    public Question insert(Question entity) {
        try {
            jdbcTemplate.update(INSERT_QUESTION, entity.getQuizId(), String.valueOf(entity.getType()), entity.getText(), entity.isActive());
        } catch (DataAccessException e) {
            throw new DatabaseException("Database access exception while question insert");
        }

        return entity;
    }

    public boolean updateQuestion(Question question) {
        int affectedRowNumber = jdbcTemplate.update(UPDATE_QUESTION,
                question.getType(),
                question.getText(),
                question.isActive(),
                question.getId());

        return affectedRowNumber > 0;
    }
}