package com.quiz.dao.mapper;

import com.quiz.entities.AchievementCategory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.swing.tree.TreePath;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AchievementCategoryMapper implements RowMapper<AchievementCategory> {
    public static final String ACHIEVEMENT_CATEGORY_ID = "id";
    public static final String ACHIEVEMENT_CATEGORY_NAME = "name";


    @Override
    public AchievementCategory mapRow(ResultSet resultSet, int i) throws SQLException {
        AchievementCategory achievementCategory = new AchievementCategory();

        achievementCategory.setId(resultSet.getInt(ACHIEVEMENT_CATEGORY_ID));
        achievementCategory.setName(resultSet.getString(ACHIEVEMENT_CATEGORY_NAME));

        return achievementCategory;
    }
}
