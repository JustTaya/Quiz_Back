package com.quiz.dao;

import com.quiz.dao.mapper.AchievementCategoryMapper;
import com.quiz.dao.mapper.AchievementMapper;
import com.quiz.dao.mapper.UserAchievementMapper;
import com.quiz.entities.Achievement;
import com.quiz.entities.AchievementCategory;
import com.quiz.entities.UserAchievement;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.quiz.dao.mapper.AchievementMapper.*;
import static com.quiz.dao.mapper.AchievementMapper.ACHIEVEMENT_CATEGORY_ID;

@Repository
@RequiredArgsConstructor
public class AchievementDao {
    private final JdbcTemplate jdbcTemplate;

    public static final String GET_ACHIEVEMENT_CATEGORIES = "SELECT id, name FROM achievement_categories";
    public static final String GET_ACHIEVEMENTS = "SELECT id, name, description, category_id FROM achievements";
    public static final String GET_ACHIEVEMENTS_BY_USER = "SELECT id, name, description, category_id, progress FROM achievements INNER JOIN users_achievements on achievements.id = users_achievements.achievement_id WHERE user_id = ?";
    public static final String GET_ACHIEVEMENTS_BY_CATEGORIES = "SELECT id, name, description, category_id FROM achievements WHERE category_id = ?";
    public static final String COUNT_TOTAL_ACHIEVEMENTS = "SELECT COUNT(*) FROM achievements";
    public static final String COUNT_ACHIEVEMENTS_OF_USER = "SELECT COUNT(achievement_id) FROM users_achievements WHERE user_id=? AND progress=100";
    public static final String GET_PROGRESS = "SELECT progress, date FROM users_achievements WHERE user_id=? AND achievement_id=?";


    public List<AchievementCategory> getAchievementCategories() {
        List<AchievementCategory> achievementCategories = jdbcTemplate.query(GET_ACHIEVEMENT_CATEGORIES, new AchievementCategoryMapper());
        if (achievementCategories.isEmpty()) {
            return null;
        }
        return achievementCategories;
    }

    public List<Achievement> getAchievements() {
        List<Achievement> achievements = jdbcTemplate.query(GET_ACHIEVEMENTS, (resultSet, i) -> {
            Achievement achievement = new Achievement();

            achievement.setId(resultSet.getInt(ACHIEVEMENT_ID));
            achievement.setName(resultSet.getString(ACHIEVEMENT_NAME));
            achievement.setDescription(resultSet.getString(ACHIEVEMENT_DESCRIPTION));
            achievement.setCategoryId(resultSet.getInt(ACHIEVEMENT_CATEGORY_ID));

            return achievement;
        });
        if (achievements.isEmpty()) {
            return null;
        }
        return achievements;
    }

    public List<Achievement> getAchievementsByCategory(int categoryId) {
        List<Achievement> achievements = jdbcTemplate.query(GET_ACHIEVEMENTS_BY_CATEGORIES, new Object[]{categoryId}, (resultSet, i) -> {
            Achievement achievement = new Achievement();

            achievement.setId(resultSet.getInt(ACHIEVEMENT_ID));
            achievement.setName(resultSet.getString(ACHIEVEMENT_NAME));
            achievement.setDescription(resultSet.getString(ACHIEVEMENT_DESCRIPTION));
            achievement.setCategoryId(resultSet.getInt(ACHIEVEMENT_CATEGORY_ID));

            return achievement;
        });
        if (achievements.isEmpty()) {
            return null;
        }
        return achievements;
    }

    public List<Achievement> getAchievementsByUser(int userId) {
        List<Achievement> achievements = jdbcTemplate.query(GET_ACHIEVEMENTS_BY_USER, new Object[]{userId}, new AchievementMapper());
        if (achievements.isEmpty()) {
            return null;
        }
        return achievements;
    }

    public Integer countAchievementsTotal() {
        Integer count = jdbcTemplate.queryForObject(COUNT_TOTAL_ACHIEVEMENTS, Integer.class);
        return count;
    }

    public Integer countAchievementsForUser(int userId) {
        Integer count = jdbcTemplate.queryForObject(COUNT_ACHIEVEMENTS_OF_USER, new Object[]{userId}, Integer.class);
        return count;
    }

    public UserAchievement getProgress(int userId, int achievementId) {
        List<UserAchievement> userAchievements;
        try {
            userAchievements = jdbcTemplate.query(GET_PROGRESS, new Object[]{userId, achievementId}, new UserAchievementMapper());
            if (userAchievements.isEmpty()) {
                return null;
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find answer by userId '%s' and achievementId '%s' database error occured", userId, achievementId));
        }

        return userAchievements.get(0);
    }
}
