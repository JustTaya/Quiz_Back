package com.quiz.dao;

import com.quiz.dao.mapper.UserMapper;
import com.quiz.entities.Gender;
import com.quiz.entities.User;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.quiz.dao.mapper.UserMapper.*;

@Repository
@RequiredArgsConstructor
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String USER_FIND_BY_EMAIL = "SELECT id, email, password FROM users WHERE email = ?";
    private static final String USER_FIND_BY_ID = "SELECT id,email,password FROM users WHERE id = ?";
    private static final String USER_GET_ALL_FOR_PROFILE_BY_ID = "SELECT id, name, surname, birthdate, gender, city, about FROM users WHERE id = ?";
    private static final String FIND_FRIENDS_BY_USER_ID = "SELECT friend_id, name, surname, rating FROM users INNER JOIN friends ON id = user_id WHERE id = ?";
    private static final String INSERT_USER = "INSERT INTO users (email, password) VALUES (?,?)";
    private static final String UPDATE_USER = "UPDATE users  SET name = ?, surname = ?, birthdate = ?, gender = ?, city = ?, about = ? WHERE id = ?";
    private static final String UPDATE_USER_PASSWORD = "UPDATE users SET password = ? WHERE id = ?";
    public static final String TABLE_USERS = "users";

    public User findByEmail(String email) {
        List<User> users;

        try {
            users = jdbcTemplate.query(
                    USER_FIND_BY_EMAIL,
                    new Object[]{email}, (resultSet, i) -> {
                        User user = new User();

                        user.setId(resultSet.getInt(USERS_ID));
                        user.setEmail(resultSet.getString(USERS_EMAIL));
                        user.setPassword(resultSet.getString(USERS_PASSWORD));

                        return user;
                    }
            );
            if (users.isEmpty()) {
                return null;
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find user by email '%s' database error occured", email));
        }

        return users.get(0);
    }

    public User findById(int id) {
        List<User> users;

        try {
            users = jdbcTemplate.query(
                    USER_FIND_BY_ID,
                    new Object[]{id}, (resultSet, i) -> {
                        User user = new User();

                        user.setId(resultSet.getInt(USERS_ID));
                        user.setEmail(resultSet.getString(USERS_EMAIL));
                        user.setPassword(resultSet.getString(USERS_PASSWORD));
                        return user;
                    }
            );
            if (users.isEmpty()) {
                return null;
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find user by id '%s' database error occured", id));
        }

        return users.get(0);
    }

    @Transactional
    public User insert(User entity) {
        int id;

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName(TABLE_USERS)
                .usingGeneratedKeyColumns(UserMapper.USERS_ID);


        Map<String, Object> parameters = new HashMap<>();
        parameters.put(UserMapper.USERS_ID, entity.getId());
        parameters.put(UserMapper.USERS_EMAIL, entity.getEmail());
        parameters.put(UserMapper.USERS_PASSWORD, entity.getPassword());


        try {
            jdbcTemplate.update(INSERT_USER, entity.getEmail(), entity.getPassword());
        } catch (DataAccessException e) {
            throw new DatabaseException("Database access exception while user insert");
        }

        return entity;
    }

    public User findProfileInfoByUserId(int id) {
        List<User> users = jdbcTemplate.query(
                USER_GET_ALL_FOR_PROFILE_BY_ID,
                new Object[]{id}, (resultSet, i) -> {
                    User user = new User();

                    user.setName(resultSet.getString(USERS_NAME));
                    user.setSurname(resultSet.getString(USERS_SURNAME));
                    user.setBirthdate(resultSet.getDate(USERS_BIRTHDATE));
                    user.setGender(Gender.valueOf(resultSet.getString(USERS_GENDER)));
                    user.setCity(resultSet.getString(USERS_CITY));
                    user.setAbout(resultSet.getString(USERS_ABOUT));

                    return user;
                });

        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }

    public List<User> findFriendByUserId(int id) {
        List<User> friends = jdbcTemplate.query(
                FIND_FRIENDS_BY_USER_ID,
                new Object[]{id}, (resultSet, i) -> {
                    User user = new User();
                    user.setId(resultSet.getInt("friend_id"));
                    user.setName(resultSet.getString(USERS_NAME));
                    user.setSurname(resultSet.getString(USERS_SURNAME));
                    user.setRating(resultSet.getInt(USERS_RATING));

                    return user;
                });

        return friends;
    }

    public boolean updateUser(User user) {
        int affectedRowNumber = jdbcTemplate.update(UPDATE_USER, user.getName(),
                user.getSurname(), user.getBirthdate(),
                user.getGender(), user.getCity(),
                user.getAbout());

        return affectedRowNumber > 0;
    }

    public boolean updatePasswordById(int id, String newPassword) {
        int affectedNumberOfRows = jdbcTemplate.update(UPDATE_USER_PASSWORD, newPassword, id);
        return affectedNumberOfRows > 0;
    }
}
