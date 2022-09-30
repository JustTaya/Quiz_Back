package com.quiz.service;

import com.quiz.dao.UserDao;
import com.quiz.entities.User;
import com.quiz.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    public User findByEmail(String email) {
        User userdb = userDao.findByEmail(email);
        if (userdb == null) {
            throw new NotFoundException("user", "email", email);
        }
        return userdb;
    }

    public User findById(int id) {
        return userDao.findById(id);
    }
}
