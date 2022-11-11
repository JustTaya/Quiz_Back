package com.quiz.entities;

import lombok.Data;

import java.sql.Date;

@Data
public class Game {
    private int id;
    private String quizName;
    private Date date;
}
