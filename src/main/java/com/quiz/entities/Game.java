package com.quiz.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class Game {
    private int id;
    private String quizName;
    private Date date;
}
