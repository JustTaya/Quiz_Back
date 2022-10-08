package com.quiz.controllers;

import com.quiz.dto.QuestionDto;
import com.quiz.entities.Question;
import com.quiz.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/quiz")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/{quizId}")
    public ResponseEntity<List<Question>> getQuestionsByQuizId(@PathVariable int quizId) {
        return ResponseEntity.ok(questionService.findQuestionsByQuizId(quizId));
    }

    @GetMapping("/{quizId}/{questionId}")
    public ResponseEntity<Question> getQuestionById(@PathVariable int quizId, @PathVariable int questionId) {
        Question question = questionService.findById(questionId);
        if (question.getQuizId() == quizId) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(question);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/new_quiz/{quiz_id}")
    public ResponseEntity<QuestionDto> insert(@RequestBody Question question, @PathVariable int quiz_id) {

        if (question.getQuizId() == quiz_id) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(questionService.insertQuestion(question));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/update_quiz/{quizId}/{questionId}")
    public ResponseEntity<String> update(@RequestBody Question question, @PathVariable int quizId, @PathVariable int questionId) {
        boolean isRecordAffected = false;
        if (question.getQuizId() == quizId && question.getId() == questionId) {
            isRecordAffected = questionService.updateQuestion(question);
        }

        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
