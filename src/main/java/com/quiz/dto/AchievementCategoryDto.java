package com.quiz.dto;

import com.quiz.entities.AchievementCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AchievementCategoryDto {
    private int id;
    private String name;
}
