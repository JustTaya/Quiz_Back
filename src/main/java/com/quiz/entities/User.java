package com.quiz.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class User {

    private int id;
    private String email;
    private String password;
    private Role role;
    private String name;
    private String surname;
    private Date birthdate;
    private Gender gender;
    private int countryId;
    private String city;
    private int rating;
    private String about;
    private boolean active;
    private NotificationStatus notification;
    private int languageId;
}
