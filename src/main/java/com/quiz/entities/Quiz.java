package com.quiz.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Quiz {

    private int id;
    private String name;
    private String image;
    private int author;
    private int categoryId;
    private Date date;
    private String description;
    private StatusType status;
    private Timestamp modificationTime;
    private String category;
    private boolean isFavorite;
    private List<String> tags;

}
