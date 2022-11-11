package com.quiz.controllers;

import com.quiz.entities.Achievement;
import com.quiz.entities.AchievementCategory;
import com.quiz.entities.UserAchievement;
import com.quiz.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/achievements")
public class AchievementController {
    private final AchievementService achievementService;

    @GetMapping
    public ResponseEntity<List<Achievement>> getAchievements() {
        return ResponseEntity.ok(achievementService.getAchievements());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Achievement>> getAchievementsByUser(@PathVariable int userId) {
        return ResponseEntity.ok(achievementService.getAchievementsByUser(userId));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<AchievementCategory>> getAchievementCategories() {
        return ResponseEntity.ok(achievementService.getAchievementCategories());
    }

    @GetMapping("/by_category/{categoryId}")
    public ResponseEntity<List<Achievement>> getAchievementCategories(@PathVariable int categoryId) {
        return ResponseEntity.ok(achievementService.findAchievementByCategory(categoryId));
    }

    @GetMapping("/count_total")
    public ResponseEntity<Integer> countAchievementsTotal() {
        return ResponseEntity.ok(achievementService.countAchievementsTotal());
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<Integer> countAchievementsForUser(@PathVariable int userId) {
        return ResponseEntity.ok(achievementService.countAchievementsForUser(userId));
    }

    @GetMapping("/progress")
    public ResponseEntity<UserAchievement> countAchievementsTotal(@RequestParam int userId, @RequestParam int achievementId) {
        UserAchievement userAchievement = achievementService.getProgress(userId, achievementId);
        return ResponseEntity.status(HttpStatus.CREATED).body(userAchievement);
    }
}
