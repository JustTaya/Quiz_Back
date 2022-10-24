package com.quiz.dto;

import com.quiz.entities.Quiz;
import com.quiz.entities.StatusType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
public class QuizDto {

    private int id;
    private String name;
    private int author;
    private int category_id;
    private Date date;
    private String description;
    private StatusType status;
    private Timestamp modificationTime;

    public QuizDto(int id, String name, int author, int category_id, Date date, String description, StatusType status, Timestamp modificationTime) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.category_id = category_id;
        this.date = date;
        this.description = description;
        this.status = status;
        this.modificationTime = modificationTime;
    }

    public QuizDto(Quiz quiz){
        this.id = quiz.getId();
        this.name = quiz.getName();
        this.author = quiz.getAuthor();
        this.category_id = quiz.getCategory_id();
        this.date = quiz.getDate();
        this.description = quiz.getDescription();
        this.status = quiz.getStatus();
        this.modificationTime = quiz.getModificationTime();
    }
}
