package com.quiz.controllers;

import com.quiz.dto.AnswerDto;
import com.quiz.entities.Answer;
import com.quiz.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/new")
    public ResponseEntity<AnswerDto> insert(@RequestBody Answer answer) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(answerService.insertAnswer(answer));
    }

    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestBody Answer answer) {
        boolean isRecordAffected = answerService.updateAnswer(answer);

        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .build();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}
