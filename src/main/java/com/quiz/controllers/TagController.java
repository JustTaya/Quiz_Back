package com.quiz.controllers;

import com.quiz.dto.TagDto;
import com.quiz.entities.Tag;
import com.quiz.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/quizzes/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/id/{tagId}")
    public ResponseEntity<Tag> getTagById(@PathVariable int tagId) {
        return ResponseEntity.ok(tagService.findById(tagId));
    }

    @GetMapping("/name/{tagName}")
    public ResponseEntity<Tag> getTagByName(@PathVariable String tagName) {
        return ResponseEntity.ok(tagService.findTagByName(tagName));
    }

    @GetMapping("/regex_name")
    public ResponseEntity<List<Tag>> getTagsByName(@RequestParam(value = "name") String tagName) {
        return ResponseEntity.ok(tagService.findTagsByName(tagName));
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<Tag>> getTagsByQuiz(@PathVariable int quizId) {
        return ResponseEntity.ok(tagService.findTagsByQuiz(quizId));
    }

    @PostMapping("/new")
    public ResponseEntity<TagDto> insert(@RequestBody Tag tag) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tagService.insertTag(tag));
    }
}
