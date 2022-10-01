package com.quiz.dao.mapper;

import com.quiz.entities.Quiz;
import com.quiz.entities.StatusType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuizMapper implements RowMapper<Quiz> {
    @Override
    public Quiz mapRow(ResultSet resultSet, int i) throws SQLException {
        Quiz quiz = new Quiz();
        quiz.setId(resultSet.getInt("id"));
        quiz.setName(resultSet.getString("name"));
        quiz.setAuthor(resultSet.getInt("author"));
        quiz.setCategoryId(resultSet.getInt("category_id"));
        quiz.setDate(resultSet.getDate("date"));
        quiz.setDescription(resultSet.getString("description"));
        quiz.setStatus(StatusType.valueOf(resultSet.getString("status")));
        quiz.setModificationTime(resultSet.getTimestamp("modification_time"));

        return quiz;
    }
}
