package com.quiz.dao.mapper;

import com.quiz.entities.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserMapper implements RowMapper<User> {

    public static final String USERS_ID = "id";
    public static final String USERS_PASSWORD = "password";
    public static final String USERS_EMAIL = "email";

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();

        user.setId(resultSet.getInt(USERS_ID));
        user.setPassword(resultSet.getString(USERS_PASSWORD));
        user.setEmail(resultSet.getString(USERS_EMAIL));
        return user;
    }
}
