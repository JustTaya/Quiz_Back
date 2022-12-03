package com.quiz.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Data
@NoArgsConstructor
public class ModeratorComment {
    private int id;
    private int moderatorId;
    private int quizId;
    private Date commentDate;
    private String comment;
}
