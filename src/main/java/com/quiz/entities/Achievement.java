package com.quiz.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Achievement {
    private int id;
    private String name;
    private String description;
    private int categoryId;
    private int progress;
}
