package com.quiz.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
public class UserAchievement {
    private int userId;
    private int achievementId;
    private int progress;
    private Date date;
}
