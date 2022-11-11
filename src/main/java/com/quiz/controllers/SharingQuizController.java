package com.quiz.controllers;


import com.quiz.dto.QuizDto;
import com.quiz.entities.Quiz;
import com.quiz.entities.ResponsePaginatedList;
import com.quiz.entities.ResponseText;
import com.quiz.entities.StatusType;
import com.quiz.service.PaginationService;
import com.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class SharingQuizController {

    private final QuizService quizService;
    private final PaginationService paginationService;

    @GetMapping("/{quizId}")
    public ResponseEntity<QuizDto> getQuiz(@PathVariable int quizId) {
        return ResponseEntity.ok(quizService.findQuizById(quizId));
    }

    @GetMapping("/{pageSize}/{pageNumber}/{userId}")
    public ResponseEntity<ResponsePaginatedList<Quiz>> getAllQuizzes(@PathVariable int pageSize, @PathVariable int pageNumber, @PathVariable int userId) {
        List<Quiz> quizzes = quizService.findAllQuizzes(userId);
        return ResponseEntity.ok(new ResponsePaginatedList<>(paginationService.paginate(quizzes, pageSize, pageNumber), quizzes.size()));
    }

    @GetMapping("/categories/{categoryId}/{pageSize}/{pageNumber}/{userId}")
    public ResponseEntity<ResponsePaginatedList<Quiz>> getQuizzesByCategory(@PathVariable int categoryId, @PathVariable int pageSize, @PathVariable int pageNumber, @PathVariable int userId) {
        List<Quiz> quizzes = quizService.findQuizzesByCategory(categoryId, userId);
        return ResponseEntity.ok(new ResponsePaginatedList<>(paginationService.paginate(quizzes, pageSize, pageNumber), quizzes.size()));
    }

    @GetMapping("/tags/{tagId}")
    public ResponseEntity<List<Quiz>> getQuizzesByTag(@PathVariable int tagId) {
        return ResponseEntity.ok(quizService.findQuizzesByTag(tagId));
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<Quiz>> getQuizzesByName(@PathVariable String name) {
        return ResponseEntity.ok(quizService.findQuizzesByName(name));
    }

    @PostMapping("/update_quiz")
    public ResponseEntity<Quiz> updateQuizInfo(@RequestBody Quiz quiz) {

        boolean isRecordAffected = quizService.updateQuiz(quiz);

        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/new_quiz")
    public ResponseEntity<QuizDto> insert(@RequestBody Quiz quiz) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quizService.insertQuiz(quiz));
    }

    @GetMapping("/get_image/{quizId}")
    public ResponseEntity<ResponseText> getQuizImage(@PathVariable int quizId) {
        return ResponseEntity.ok(new ResponseText(new String(Base64.getEncoder().encode(quizService.getImageByQuizId(quizId)))));
    }

    @PostMapping("/new_image/{quizId}")
    public ResponseEntity<String> changeQuizImage(@RequestParam(value = "image") MultipartFile image, @PathVariable int quizId) {
        boolean isRecordAffected = quizService.updateQuizImage(image, quizId);
        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    @PostMapping("/add_tag")
    public ResponseEntity<String> addTagToQuiz(@RequestParam(value = "quizId") int quizId, @RequestParam(value = "tagId") int tagId) {
        boolean isRecordAffected = quizService.addTag(quizId, tagId);
        return ResponseEntity.status(isRecordAffected ? HttpStatus.CREATED : HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/top_quizzes")
    public ResponseEntity<List<Quiz>> getTopQuizzes(@RequestParam(value = "limit") int limit) {
        return ResponseEntity.ok(quizService.findTopPopularQuizzes(limit));
    }

    @GetMapping("/top_quizzes/{categoryId}")
    public ResponseEntity<List<Quiz>> getTopQuizzesByCategory(@PathVariable int categoryId, @RequestParam(value = "limit") int limit) {
        return ResponseEntity.ok(quizService.findTopPopularQuizzesByCategory(categoryId, limit));
    }

    @GetMapping("/recent_quizzes/{userId}")
    public ResponseEntity<List<Quiz>> getRecentQuizzes(@PathVariable int userId, @RequestParam(value = "limit") int limit) {
        return ResponseEntity.ok(quizService.findRecentGames(userId, limit));
    }

    @GetMapping("/filter/{searchByUser}/{pageSize}/{pageNumber}/{userId}")
    public ResponseEntity<ResponsePaginatedList<Quiz>> getFilteredQuizzes(@PathVariable String searchByUser, @PathVariable int pageSize, @PathVariable int pageNumber, @PathVariable int userId){
        List<Quiz> quizzes = quizService.getQuizzesByFilter(searchByUser, userId);
        return ResponseEntity.ok(new ResponsePaginatedList<Quiz>(paginationService.paginate(quizzes, pageSize, pageNumber), quizzes.size()));
    }

    @PostMapping("/mark/{quizId}/{userId}")
    public ResponseEntity<String> markQuizAsFavorite (@PathVariable int quizId, @PathVariable int userId){
        boolean isRecordAffected = quizService.markQuizAsFavorite(quizId, userId);
        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/unmark/{quizId}/{userId}")
    public ResponseEntity<String> unmarkQuizAsFavorite (@PathVariable int quizId, @PathVariable int userId){
        boolean isRecordAffected = quizService.unmarkQuizAsFavorite(quizId, userId);
        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<Quiz>> getRecommendations(@PathVariable int userId, @RequestParam(value = "limit") int limit) {
        return ResponseEntity.ok(quizService.findRecommendations(userId, limit));
    }

    @GetMapping("/recommendations/friends/{userId}")
    public ResponseEntity<List<Quiz>> getRecommendationsByFriends(@PathVariable int userId, @RequestParam(value = "limit") int limit) {
        return ResponseEntity.ok(quizService.findRecommendationsByFriends(userId, limit));
    }

    @GetMapping("/popular/{limit}")
    public ResponseEntity<List<Quiz>> getPopularQuizzes(@PathVariable int limit) {
        return ResponseEntity.ok(quizService.findPopularQuizzes(limit));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<QuizDto>> getQuizzesByStatus(@PathVariable StatusType status){
        return ResponseEntity.ok(quizService.findQuizzesByStatus(status));
    }
}
