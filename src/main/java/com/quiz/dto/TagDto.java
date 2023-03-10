package com.quiz.dto;

import com.quiz.entities.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TagDto {
    private int id;
    private String name;
}
