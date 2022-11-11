package com.quiz.dao;

import com.quiz.entities.Category;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.quiz.dao.mapper.CategoryMapper.CATEGORY_ID;
import static com.quiz.dao.mapper.CategoryMapper.CATEGORY_NAME;

@Repository
@RequiredArgsConstructor
public class CategoryDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String CATEGORY_BY_NAME = "SELECT id, name FROM categories WHERE name=?";
    private static final String CATEGORIES_ALL = "SELECT id, name FROM categories";

    public Category findByName(String name) {
        List<Category> categories;

        try {
            categories = jdbcTemplate.query(
                    CATEGORY_BY_NAME,
                    new Object[]{name},
                    (resultSet, i) -> {
                        Category category = new Category();

                        category.setId(resultSet.getInt(CATEGORY_ID));
                        category.setName(resultSet.getString(CATEGORY_NAME));

                        return category;
                    });
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new DatabaseException(String.format("Find category by id '%s' database error occured", name));
        }

        return categories.get(0);
    }

    public List<Category> getAllCategories() {
        List<Category> categories;

        categories = jdbcTemplate.query(
                CATEGORIES_ALL,
                (resultSet, i) -> {
                    Category category = new Category();

                    category.setId(resultSet.getInt(CATEGORY_ID));
                    category.setName(resultSet.getString(CATEGORY_NAME));

                    return category;
                });

        return categories;
    }
}
