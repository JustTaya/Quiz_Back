package com.quiz.controllers;

import com.quiz.entities.ResponseToken;
import com.quiz.service.AuthService;
import com.quiz.dto.UserDto;
import com.quiz.entities.User;
import com.quiz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody User user){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(user));
    }

    @PostMapping(value ="/login",  produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseToken login(@RequestBody User user) {
        return new ResponseToken(authService.login(user), String.valueOf(userService.getUserIdByEmail(user.getEmail())), user.getEmail(), String.valueOf(userService.getUserRoleByEmail(user.getEmail())));
    }
}
