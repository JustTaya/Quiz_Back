package com.quiz.entities;

import lombok.Data;

@Data
public class Achievement {
    private int id;
    private String name;
    private String description;
    private int categoryId;
    private int progress;
}
