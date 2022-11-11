package com.quiz.controllers;

import com.quiz.entities.User;
import com.quiz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @GetMapping("/rating/{userId}")
    public ResponseEntity<Integer> getRatingByUser(@PathVariable int userId) {
        return ResponseEntity.ok(userService.getRatingByUser(userId));
    }

    @GetMapping("/rating/range/{userId}")
    public ResponseEntity<List<User>> getRatingByUser(@PathVariable int userId, @RequestParam(value = "range") int range) {
        return ResponseEntity.ok(userService.getRatingInRange(userId, range));
    }

    @GetMapping("/rating")
    public ResponseEntity<List<User>> getRating(@RequestParam(value = "from") int from, @RequestParam(value = "to") int to) {
        return ResponseEntity.ok(userService.getRating(from, to));
    }
}
