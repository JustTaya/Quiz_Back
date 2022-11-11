package com.quiz.dto;

import com.quiz.entities.Category;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDto {
    private int id;
    private String name;
}
