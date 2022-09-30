package com.quiz.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

    private int id;

    private String password;

    private String email;
}
