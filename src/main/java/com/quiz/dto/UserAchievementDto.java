package com.quiz.dto;

import com.quiz.entities.UserAchievement;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
public class UserAchievementDto {
    private int userId;
    private int achievementId;
    private int progress;
    private Date date;
}
