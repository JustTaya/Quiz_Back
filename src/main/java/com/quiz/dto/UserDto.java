package com.quiz.dto;

import com.quiz.entities.Gender;
import com.quiz.entities.NotificationStatus;
import com.quiz.entities.Role;
import com.quiz.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private int id;
    private String email;
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

    public UserDto(User user) {
        id = user.getId();
        email = user.getEmail();
        role = user.getRole();
    }
}
