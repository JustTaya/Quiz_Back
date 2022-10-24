package com.quiz.controllers;


import com.quiz.dto.QuizDto;
import com.quiz.entities.Quiz;
import com.quiz.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/quizzes")
public class SharingQuizController {

    @Autowired
    QuizService quizService;

    @GetMapping("/{quizId}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable int quizId){
        return ResponseEntity.ok(quizService.findQuizById(quizId));
    }

    @GetMapping("")
    public ResponseEntity<List<Quiz>> getAllQuizzes(){
        return ResponseEntity.ok(quizService.findAllQuizzes());
    }

    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<List<Quiz>> getQuizzesByCategory(@PathVariable int categoryId) {
        return ResponseEntity.ok(quizService.findQuizzesByCategory(categoryId));
    }

    @GetMapping("/tags/{tagId}")
    public ResponseEntity<List<Quiz>> getQuizzesByTag(@PathVariable int tagId){
        return ResponseEntity.ok(quizService.findQuizzesByTag(tagId));
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<Quiz>> getQuizzesByName(@PathVariable String name){
        return ResponseEntity.ok(quizService.findQuizzesByName(name));
    }

    @PostMapping("/update_quiz")
    public ResponseEntity<Quiz> updateQuizInfo(@RequestBody Quiz quiz){

        boolean isRecordAffected = quizService.updateQuiz(quiz);

        if (isRecordAffected){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/new_quiz")
    public ResponseEntity<QuizDto> insert(@RequestBody Quiz quiz){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quizService.insertQuiz(quiz));
    }


}
