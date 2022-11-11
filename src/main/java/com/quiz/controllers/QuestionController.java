package com.quiz.controllers;

import com.quiz.dto.QuestionDto;
import com.quiz.entities.Question;
import com.quiz.entities.ResponseText;
import com.quiz.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/quiz/question")
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<Question>> getQuestionsByQuizId(@PathVariable int quizId) {
        return ResponseEntity.ok(questionService.findQuestionsByQuizId(quizId));
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<Question> getQuestionById(@PathVariable int questionId) {
        Question question = questionService.findById(questionId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(question);
    }

    @PostMapping("/new")
    public ResponseEntity<QuestionDto> insert(@RequestBody Question question) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.insertQuestion(question));
    }

    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody Question question) {
        boolean isRecordAffected = questionService.updateQuestion(question);

        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    @GetMapping("/get_image/{questionId}")
    public ResponseEntity<ResponseText> getQuestionImage(@PathVariable int questionId) {
        return ResponseEntity.ok(new ResponseText(new String(Base64.getEncoder().encode(questionService.getQuestionByQuestionId(questionId)))));
    }

    @PostMapping("/new_image/{questionId}")
    public ResponseEntity<String> changeQuestionImage(@RequestParam(value = "image") MultipartFile image, @PathVariable int questionId) {
        boolean isRecordAffected = questionService.updateImageByQuestionId(image, questionId);
        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
