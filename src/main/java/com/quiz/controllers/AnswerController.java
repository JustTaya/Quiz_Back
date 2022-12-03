package com.quiz.controllers;

import com.quiz.dto.AnswerDto;
import com.quiz.entities.Answer;
import com.quiz.entities.ResponseText;
import com.quiz.service.AnswerService;
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
@RequestMapping("/quiz/answer")
public class AnswerController {
    private final AnswerService answerService;

    @GetMapping("/{answerId}")
    public ResponseEntity<Answer> getAnswerById(@PathVariable int answerId) {
        Answer answer = answerService.findById(answerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(answer);
    }

    @GetMapping("/question/{questionId}")
    public ResponseEntity<List<Answer>> getAnswersByQuestionId(@PathVariable int questionId) {
        return ResponseEntity.ok(answerService.findAnswersByQuestionId(questionId));
    }

    @GetMapping("/get_image/{answerId}")
    public ResponseEntity<ResponseText> getAnswerImage(@PathVariable int answerId) {
        return ResponseEntity.ok(new ResponseText(new String(Base64.getEncoder().encode(answerService.getImageByAnswerId(answerId)))));
    }

    @PostMapping("/new_image/{answerId}")
    public ResponseEntity<String> changeAnswerImage(@RequestParam(value = "image") MultipartFile image, @PathVariable int answerId) {
        boolean isRecordAffected = answerService.updateAnswerImage(image, answerId);
        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
