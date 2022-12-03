package com.quiz.dto;

import com.quiz.entities.StatusType;
import com.quiz.entities.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class QuizDto {
    private Integer id;
    private String name;
    private String image;
    private int author;
    private int categoryId;
    private Date date;
    private String description;
    private StatusType status;
    private Timestamp modificationTime;
    private String category;
    private String authorName;
    private String authorSurname;
    private String authorEmail;
    private List<QuestionDto> questions;
    private List<Tag> tags;
    private boolean favorite;
    private boolean changed;
    private String moderatorComment;
}
