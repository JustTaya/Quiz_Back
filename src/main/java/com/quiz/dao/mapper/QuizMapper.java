package com.quiz.dao.mapper;

import com.quiz.entities.Quiz;
import com.quiz.entities.StatusType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class QuizMapper implements RowMapper<Quiz> {
    public static final String QUIZ_ID = "id";
    public static final String QUIZ_NAME = "name";
    public static final String QUIZ_AUTHOR = "author";
    public static final String QUIZ_CATEGORY = "category_id";
    public static final String QUIZ_DATE = "date";
    public static final String QUIZ_DESCRIPTION = "description";
    public static final String QUIZ_STATUS = "status";
    public static final String QUIZ_MODIFICATION_TIME = "modification_time";

    @Override
    public Quiz mapRow(ResultSet resultSet, int rowNum) throws SQLException {
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
}
