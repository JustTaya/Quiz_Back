package com.quiz.dto;

import com.quiz.entities.Quiz;
import com.quiz.entities.StatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDto {

    private int id;
    private String name;
    private int author;
    private int categoryId;
    private Date date;
    private String description;
    private StatusType status;
    private Timestamp modificationTime;

    public QuizDto(Quiz quiz){
        this.id = quiz.getId();
        this.name = quiz.getName();
        this.author = quiz.getAuthor();
        this.categoryId = quiz.getCategoryId();
        this.date = quiz.getDate();
        this.description = quiz.getDescription();
        this.status = quiz.getStatus();
        this.modificationTime = quiz.getModificationTime();
    }
}
