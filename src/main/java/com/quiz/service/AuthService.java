package com.quiz.service;

import com.quiz.security.TokenProvider;
import com.quiz.dao.UserDao;
import com.quiz.dto.UserDto;
import com.quiz.entities.User;
import com.quiz.exceptions.EmailExistException;
import com.quiz.exceptions.NotFoundException;
import com.quiz.exceptions.PasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDao userDao;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto register(User user) {
        User userdb =userDao.findByEmail(user.getEmail());
        if(userdb != null){
            throw new EmailExistException("User with this email already exist");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.insert(user);
        return new UserDto(user);
    }

    public String login(User user) {
        User userdb = userDao.findByEmail(user.getEmail());
        if (userdb == null) {
            throw new NotFoundException("user", "email", user.getEmail());
        }
        if(!passwordEncoder.matches(user.getPassword(), userdb.getPassword())){
            throw new PasswordException();
        }
        return tokenProvider.createToken(userdb.getEmail());
    }
}
