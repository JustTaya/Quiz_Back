package com.quiz.dto;

import com.quiz.entities.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDto {
    private int id;
    private String name;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }

    public CategoryDto(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
