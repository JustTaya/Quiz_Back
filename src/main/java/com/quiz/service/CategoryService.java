package com.quiz.service;

import com.quiz.dao.CategoryDao;
import com.quiz.entities.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryDao categoryDao;

    public Category findByName(String name) {
        return categoryDao.findByName(name);
    }

    public List<Category> getAllCategories() {
        return categoryDao.getAllCategories();
    }
}
