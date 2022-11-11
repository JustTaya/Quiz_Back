package com.quiz.dao;

import com.quiz.dao.mapper.UserMapper;
import com.quiz.entities.Gender;
import com.quiz.entities.NotificationStatus;
import com.quiz.entities.Role;
import com.quiz.exceptions.DatabaseException;
import com.quiz.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.quiz.dao.mapper.UserMapper.*;

@Repository
@RequiredArgsConstructor
public class UserDao {

    private final JdbcTemplate jdbcTemplate;

    private final static String USER_FIND_BY_EMAIL = "SELECT id, email, password FROM users WHERE email = ?";
    private final static String USER_FIND_BY_ID = "SELECT id,email,name, surname,password FROM users WHERE id = ?";
    private final static String USER_GET_ALL_FOR_PROFILE_BY_ID = "SELECT id, email, name, surname, birthdate, gender, city, about, role FROM users WHERE id = ?";
    private final static String FIND_FRIENDS_BY_USER_ID = "SELECT id, email, name, surname, rating FROM users where id in (SELECT friend_id FROM users INNER JOIN friends ON user_id = id WHERE id = ?)";
    private final static String FIND_FRIENDS_BY_USER_ID_ORDER_BY = "SELECT id, email, name, surname, rating FROM users where id in (SELECT friend_id FROM users INNER JOIN friends ON user_id = id WHERE id = ?)";
    private final static String INSERT_USER = "INSERT INTO users (email, password, role) VALUES (?,?,?::role_type)";
    private final static String UPDATE_USER = "UPDATE users  SET name = ?, surname = ?, birthdate = ?, gender = ?::gender_type, city = ?, about = ? WHERE id = ?";
    private final static String UPDATE_USER_PASSWORD = "UPDATE users SET password = ? WHERE id = ?";
    private final static String UPDATE_USER_IMAGE = "UPDATE users SET image = ? WHERE id = ?";
    private final static String GET_USER_ID_BY_EMAIL = "SELECT id FROM users WHERE email = ?";
    private final static String GET_USER_IMAGE_BY_USER_ID = "SELECT image FROM users WHERE id = ?";
    private final static String UPDATE_NOTIFICATION_STATUS = "UPDATE users SET notifications = ?::user_notification_type WHERE id = ?";
    private final static String GET_NOTIFICATION = "SELECT notifications from users WHERE id = ?";
    private final static String FILTER_FRIENDS_BY_USER_ID = "SELECT id, email, name, surname, rating FROM users where (id in (SELECT friend_id FROM users INNER JOIN friends ON user_id = id WHERE id = ?)) AND (CONCAT(name, ' ', surname) ~*?  OR rating::text ~* ?)";

    private final static String INSERT_GAME_SCORE = "INSERT INTO score (user_id, game_id, score) VALUES(?, ?, ?)";

    private final static String GET_RATING_BY_USER_ID = "SELECT rowNumb FROM (SELECT id, ROW_NUMBER() OVER (ORDER BY rating DESC) AS rowNumb FROM users) AS irN WHERE id=?";
    private final static String GET_RATING = "SELECT id, name, surname, rating, ROW_NUMBER() OVER (ORDER BY rating DESC) AS rowNumb FROM users LIMIT ? OFFSET ?";
    private static final String GET_RATING_IN_RANGE = "WITH numbereduserstable AS (SELECT id, name, surname, rating, ROW_NUMBER() OVER (ORDER BY rating DESC) AS row_number FROM users), current AS (SELECT row_number FROM numbereduserstable WHERE id = ?) SELECT numbereduserstable.* FROM numbereduserstable, current WHERE ABS(numbereduserstable.row_number - current.row_number) <= ? ORDER BY numbereduserstable.row_number";
    public static final String TABLE_USERS = "users";
    private final static String UPDATE_USER_ACTIVE_STATUS = "UPDATE users SET active= NOT active WHERE id = ?";
    private final static String FIND_ADMINS_USERS = "SELECT id,email,name,surname,role,active FROM users WHERE role = 'ADMIN' OR role = 'MODERATOR' OR role = 'SUPER_ADMIN'";
    private final static String DELETE_USER="DELETE FROM users WHERE id = ?";
    private final static String GET_USER_ROLE_BY_EMAIL = "SELECT role FROM users WHERE email = ?";

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
                        user.setName(resultSet.getString(USERS_NAME));
                        user.setSurname(resultSet.getString(USERS_SURNAME));
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
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate.getDataSource())
                .withTableName(TABLE_USERS)
                .usingGeneratedKeyColumns(UserMapper.USERS_ID);


        Map<String, Object> parameters = new HashMap<>();
        parameters.put(UserMapper.USERS_ID, entity.getId());
        parameters.put(UserMapper.USERS_EMAIL, entity.getEmail());
        parameters.put(UserMapper.USERS_PASSWORD, entity.getPassword());
        parameters.put(UserMapper.USERS_ROLE, entity.getRole());

        try {
            jdbcTemplate.update(INSERT_USER, entity.getEmail(), entity.getPassword(), entity.getRole().toString());
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

                    user.setEmail(resultSet.getString(USERS_EMAIL));
                    user.setName(resultSet.getString(USERS_NAME));
                    user.setSurname(resultSet.getString(USERS_SURNAME));
                    user.setBirthdate(resultSet.getDate(USERS_BIRTHDATE));
                    user.setGender(Gender.valueOf(resultSet.getString(USERS_GENDER)));
                    user.setCity(resultSet.getString(USERS_CITY));
                    user.setAbout(resultSet.getString(USERS_ABOUT));
                    user.setRole(Role.valueOf(resultSet.getString(USERS_ROLE)));

                    return user;
                });

        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }

    public List<User> findFriendByUserId(int id, String sort) {

        List<User> friends = jdbcTemplate.query(
                sort.isEmpty() ? FIND_FRIENDS_BY_USER_ID : FIND_FRIENDS_BY_USER_ID + "ORDER BY " + sort,
                new Object[]{id},
                (resultSet, i) -> {
                    User user = new User();
                    user.setId(resultSet.getInt(USERS_ID));
                    user.setEmail(resultSet.getString(USERS_EMAIL));
                    user.setName(resultSet.getString(USERS_NAME));
                    user.setSurname(resultSet.getString(USERS_SURNAME));
                    user.setRating(resultSet.getInt(USERS_RATING));

                    return user;
                });

        if (friends.isEmpty()) {
            return null;
        }

        return friends;
    }

    public List<User> findAdminsUsers() {
        List<User> adminsUsers = jdbcTemplate.query(
                FIND_ADMINS_USERS, (resultSet, i) -> {
                    User user = new User();
                    user.setId(resultSet.getInt(USERS_ID));
                    user.setEmail(resultSet.getString(USERS_EMAIL));
                    user.setName(resultSet.getString(USERS_NAME));
                    user.setSurname(resultSet.getString(USERS_SURNAME));
                    user.setRole(Role.valueOf(resultSet.getString(USERS_ROLE).trim()));
                    user.setActive(resultSet.getBoolean(USERS_ACTIVE));

                    return user;
                });

        if (adminsUsers.isEmpty()) {
            return null;
        }

        return adminsUsers;
    }

    public boolean updateUser(User user) {
        int affectedRowNumber = jdbcTemplate.update(UPDATE_USER, user.getName(),
                user.getSurname(), user.getBirthdate(),
                String.valueOf(user.getGender()), user.getCity(),
                user.getAbout(), user.getId());

        return affectedRowNumber > 0;
    }

    public boolean updatePasswordById(int id, String newPassword) {
        int affectedNumberOfRows = jdbcTemplate.update(UPDATE_USER_PASSWORD, newPassword, id);
        return affectedNumberOfRows > 0;
    }

    public boolean updateStatusById(int id) {
        int affectedNumberOfRows = jdbcTemplate.update(UPDATE_USER_ACTIVE_STATUS, id);
        return affectedNumberOfRows > 0;
    }

    public int getUserIdByEmail(String email) {
        List<Integer> id = jdbcTemplate.query(GET_USER_ID_BY_EMAIL, new Object[]{email}, (resultSet, i) -> resultSet.getInt("id"));

        return id.get(0);
    }

    public boolean updateProfileImage(MultipartFile image, int userId) {
        int affectedNumbersOfRows = 0;
        try {
            affectedNumbersOfRows = jdbcTemplate.update(UPDATE_USER_IMAGE, image.getBytes(), userId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return affectedNumbersOfRows > 0;
    }


    public byte[] getUserImageByUserId(int userId) {
        List<byte[]> imageBlob = jdbcTemplate.query(GET_USER_IMAGE_BY_USER_ID, new Object[]{userId}, (resultSet, i) -> resultSet.getBytes("image"));

        if (imageBlob.get(0) == null) {
            return null;
        }
        return imageBlob.get(0);
    }

    public boolean updateNotificationStatus(String status, int userId) {
        int affectedNumberOfRows = jdbcTemplate.update(UPDATE_NOTIFICATION_STATUS, status, userId);
        return affectedNumberOfRows > 0;
    }

    public NotificationStatus getUserNotification(int userId) {
        return NotificationStatus.valueOf(jdbcTemplate.query(GET_NOTIFICATION, new Object[]{userId}, (resultSet, i) -> resultSet.getString("notifications")).get(0));
    }

    public Integer getRatingByUser(int userId) {
        return jdbcTemplate.queryForObject(GET_RATING_BY_USER_ID, new Object[]{userId}, Integer.class);
    }

    public List<User> getRating(int from, int to) {
        return jdbcTemplate.query(GET_RATING, new Object[]{to, from}, (resultSet, i) -> {
            User user = new User();

            user.setId(resultSet.getInt(USERS_ID));
            user.setName(resultSet.getString(USERS_NAME));
            user.setSurname(resultSet.getString(USERS_SURNAME));
            user.setRating(resultSet.getInt(USERS_RATING));

            return user;
        });
    }

    public List<User> getRatingInRange(int userId, int range) {
        return jdbcTemplate.query(GET_RATING_IN_RANGE, new Object[]{userId, range}, (resultSet, i) -> {
            User user = new User();

            user.setId(resultSet.getInt(USERS_ID));
            user.setName(resultSet.getString(USERS_NAME));
            user.setSurname(resultSet.getString(USERS_SURNAME));
            user.setRating(resultSet.getInt(USERS_RATING));

            return user;
        });
    }

    public List<User> filterFriendByUserId(String userSearch, int userId, String sort) {
        return jdbcTemplate.query(sort.isEmpty() ? FILTER_FRIENDS_BY_USER_ID : FILTER_FRIENDS_BY_USER_ID + "ORDER BY " + sort,
                new Object[]{userId, userSearch, userSearch},
                (resultSet, i) -> {
                    User user = new User();
                    user.setId(resultSet.getInt(USERS_ID));
                    user.setEmail(resultSet.getString(USERS_EMAIL));
                    user.setName(resultSet.getString(USERS_NAME));
                    user.setSurname(resultSet.getString(USERS_SURNAME));
                    user.setRating(resultSet.getInt(USERS_RATING));

                    return user;
                });
    }

    public void insertUserScore(int userId, int gameId, int score) {
        jdbcTemplate.update(INSERT_GAME_SCORE,
                userId, gameId, score);
    }

    public void deleteUserById(int id) {
        jdbcTemplate.update(DELETE_USER,id);
    }

    public String getUserRoleByEmail(String email) {
        List<String> role = jdbcTemplate.query(GET_USER_ROLE_BY_EMAIL, new Object[]{email}, (resultSet, i) -> resultSet.getString("role"));

        return role.get(0);
    }
}
