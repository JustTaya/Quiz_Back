package com.quiz.dao.mapper;

import com.quiz.entities.Role;
import com.quiz.entities.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminUserMapper implements RowMapper<User> {

    public static final String USERS_ID = "id";
    public static final String USERS_EMAIL = "email";
    public static final String USERS_ROLE = "role";
    public static final String USERS_NAME = "name";
    public static final String USERS_SURNAME = "surname";
    public static final String USERS_ACTIVE = "active";

    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();

        user.setId(resultSet.getInt(USERS_ID));
        user.setEmail(resultSet.getString(USERS_EMAIL));
        user.setRole(Role.valueOf(resultSet.getString(USERS_ROLE).trim()));
        user.setName(resultSet.getString(USERS_NAME));
        user.setSurname(resultSet.getString(USERS_SURNAME));
        user.setActive(resultSet.getBoolean(USERS_ACTIVE));

        return user;
    }
}
