package com.quiz.dao.mapper;

import com.quiz.entities.UserAchievement;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserAchievementMapper implements RowMapper<UserAchievement> {
    public static final String USER_ID = "user_id";
    public static final String ACHIEVEMENT_CATEGORY_ID = "achievement_id";
    public static final String USER_ACHIEVEMENT_PROGRESS = "progress";
    public static final String USER_ACHIEVEMENT_DATE = "date";

    @Override
    public UserAchievement mapRow(ResultSet resultSet, int i) throws SQLException {
        UserAchievement userAchievement = new UserAchievement();

        userAchievement.setUserId(resultSet.getInt(USER_ID));
        userAchievement.setAchievementId(resultSet.getInt(ACHIEVEMENT_CATEGORY_ID));
        userAchievement.setProgress(resultSet.getInt(USER_ACHIEVEMENT_PROGRESS));
        userAchievement.setDate(resultSet.getDate(USER_ACHIEVEMENT_DATE));

        return userAchievement;
    }
}
