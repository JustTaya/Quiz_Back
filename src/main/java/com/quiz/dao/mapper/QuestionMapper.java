package com.quiz.dao.mapper;

import com.quiz.entities.Question;
import com.quiz.entities.QuestionType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class QuestionMapper implements RowMapper<Question> {

    public static final String QUESTION_ID = "id";
    public static final String QUESTION_QUIZ_ID = "quiz_id";
    public static final String QUESTION_TYPE = "type";
    public static final String QUESTION_TEXT = "text";
    public static final String QUESTION_ACTIVE = "active";
    public static final String QUESTION_LANGUAGE_ID = "language_id";

    @Override
    public Question mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Question question = new Question();

        question.setId(resultSet.getInt(QUESTION_ID));
        question.setQuizId(resultSet.getInt(QUESTION_QUIZ_ID));
        question.setType(QuestionType.valueOf(resultSet.getString(QUESTION_TYPE)));
        question.setText(resultSet.getString(QUESTION_TEXT));
        question.setActive(resultSet.getBoolean(QUESTION_ACTIVE));
        question.setLanguageId(resultSet.getInt(QUESTION_LANGUAGE_ID));

        return question;
    }
}