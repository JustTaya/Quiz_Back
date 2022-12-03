package com.quiz.dto;

import com.quiz.entities.ModeratorComment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
public class ModeratorCommentDto {

    private int id;
    private int moderatorId;
    private int quizId;
    private Date commentDate;
    private String comment;
    private String moderatorName;
    private String moderatorSurname;
    private String moderatorEmail;
}
