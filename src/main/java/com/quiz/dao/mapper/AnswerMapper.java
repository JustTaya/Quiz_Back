package com.quiz.dao.mapper;

import com.quiz.entities.Answer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AnswerMapper implements RowMapper<Answer> {

    public static final String ANSWER_ID = "id";
    public static final String ANSWER_QUESTION_ID = "question_id";
    public static final String ANSWER_TEXT = "text";
    public static final String ANSWER_CORRECT = "correct";
    public static final String ANSWER_NEXT_ANSWER_ID = "next_answer_id";

    @Override
    public Answer mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Answer answer = new Answer();

        answer.setId(resultSet.getInt(ANSWER_ID));
        answer.setQuestionId(resultSet.getInt(ANSWER_QUESTION_ID));
        answer.setText(resultSet.getString(ANSWER_TEXT));
        answer.setCorrect(resultSet.getBoolean(ANSWER_CORRECT));
        answer.setNextAnswerId(resultSet.getInt(ANSWER_NEXT_ANSWER_ID));

        return answer;
    }
}
