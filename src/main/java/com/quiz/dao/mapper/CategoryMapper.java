package com.quiz.dao.mapper;

import com.quiz.entities.Category;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryMapper implements RowMapper<Category> {
    public static final String CATEGORY_ID = "id";
    public static final String CATEGORY_NAME = "name";

    @Override
    public Category mapRow(ResultSet resultSet, int i) throws SQLException {
        Category category = new Category();

        category.setId(resultSet.getInt(CATEGORY_ID));
        category.setName(resultSet.getString(CATEGORY_NAME));

        return category;
    }
}
