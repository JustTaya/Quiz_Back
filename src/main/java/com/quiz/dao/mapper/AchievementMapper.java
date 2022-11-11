package com.quiz.dao.mapper;

import com.quiz.entities.Achievement;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AchievementMapper implements RowMapper<Achievement> {
    public static final String ACHIEVEMENT_ID = "id";
    public static final String ACHIEVEMENT_NAME = "name";
    public static final String ACHIEVEMENT_DESCRIPTION = "description";
    public static final String ACHIEVEMENT_CATEGORY_ID = "category_id";
    public static final String ACHIEVEMENT_PROGRESS = "progress";

    @Override
    public Achievement mapRow(ResultSet resultSet, int i) throws SQLException {
        Achievement achievement = new Achievement();

        achievement.setId(resultSet.getInt(ACHIEVEMENT_ID));
        achievement.setName(resultSet.getString(ACHIEVEMENT_NAME));
        achievement.setDescription(resultSet.getString(ACHIEVEMENT_DESCRIPTION));
        achievement.setCategoryId(resultSet.getInt(ACHIEVEMENT_CATEGORY_ID));
        achievement.setProgress(resultSet.getInt(ACHIEVEMENT_PROGRESS));

        return achievement;
    }
}
