package com.quiz.controllers;

import com.quiz.dao.GameDao;
import com.quiz.dto.GameDto;
import com.quiz.dto.QuizDto;
import com.quiz.entities.*;
import com.quiz.service.PaginationService;
import com.quiz.service.QuizService;
import com.quiz.service.StoreFileService;
import com.quiz.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@CrossOrigin
public class ProfileController {

    private final StoreFileService storeFileService;
    private final UserService userRepo;
    private final QuizService quizService;
    private final PaginationService paginationService;
    private final GameDao gameDao;

    @GetMapping("/myprofile/{userId}")
    public ResponseEntity<User> getUserProfile(@PathVariable int userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userRepo.findProfileInfoByUserId(userId));
    }

    @GetMapping("/myfriends/{pageSize}/{pageNumber}/{userId}")
    public ResponseEntity<ResponcePaginatedList<User>> getFriends(@PathVariable int pageSize,
                                                                  @PathVariable int pageNumber,
                                                                  @PathVariable int userId,
                                                                  @RequestParam(defaultValue = "", required = false, value = "sort") String sort) {
        List<User> friends = userRepo.findFriendByUserId(userId, sort);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(friends, pageSize, pageNumber), friends.size()));
    }

    @GetMapping("/myfriends/{userSearch}/{pageSize}/{pageNumber}/{userId}")
    public ResponseEntity<ResponcePaginatedList<User>> getFriends(@PathVariable String userSearch,
                                                                  @PathVariable int pageSize,
                                                                  @PathVariable int pageNumber,
                                                                  @PathVariable int userId,
                                                                  @RequestParam(defaultValue = "", required = false, value = "sort") String sort) {
        List<User> friends = userRepo.filterFriendByUserId(userSearch, userId, sort);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(friends, pageSize, pageNumber), friends.size()));
    }

    @GetMapping("/adminUsers/{pageSize}/{pageNumber}/{userId}")
    public ResponseEntity<ResponcePaginatedList<User>> getAdminUsers(@PathVariable int pageSize, @PathVariable int pageNumber, @PathVariable int userId) {
        List<User> users = userRepo.findAdminsUsers(userId);
        return ResponseEntity.ok(new ResponcePaginatedList<User>(paginationService.paginate(users, pageSize, pageNumber), users.size()));
    }

    @GetMapping("/adminUsers/filter/{searchByUser}/{pageSize}/{pageNumber}/{userId}")
    public ResponseEntity<ResponcePaginatedList<User>> getFilteredUsers(@PathVariable String searchByUser,
                                                                        @PathVariable int pageSize,
                                                                        @PathVariable int pageNumber,
                                                                        @PathVariable int userId) {
        List<User> users = userRepo.getUsersByFilter(searchByUser, userId);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(users, pageSize, pageNumber), users.size()));
    }

    @GetMapping("/roleStatus/{role}/{status}/{pageSize}/{pageNumber}/{userId}")
    public ResponseEntity<ResponcePaginatedList<User>> getUsersByRoleStatus(@PathVariable String role,
                                                                            @PathVariable String status,
                                                                            @PathVariable int pageSize,
                                                                            @PathVariable int pageNumber,
                                                                            @PathVariable int userId) {
        List<User> users = userRepo.findUsersByRoleStatus(role, status, userId);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(users, pageSize, pageNumber), users.size()));
    }

    @PostMapping("/myprofile/update")
    public ResponseEntity<User> updateUserProfile(@RequestBody User user) {
        boolean isRecordAffected = userRepo.updateUser(user);

        if (isRecordAffected) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("updatePassword/{userId}")
    public ResponseEntity<String> updatePassword(@RequestBody String newPassword, @PathVariable int userId) {
        boolean isRecordAffected = userRepo.updatePasswordById(userId, newPassword);

        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/myquizzes/{pageSize}/{pageNumber}/{userId}")
    public ResponseEntity<ResponcePaginatedList<Quiz>> getUserQuizzes(@PathVariable int pageSize,
                                                                      @PathVariable int pageNumber,
                                                                      @PathVariable int userId,
                                                                      @RequestParam(defaultValue = "", required = false, value = "sort") String sort) {
        List<Quiz> quizzes = quizService.findQuizzesCreatedByUserId(userId, sort);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, pageSize, pageNumber), quizzes.size()));
    }

    @GetMapping("/myquizzes/{userSearch}/{pageSize}/{pageNumber}/{userId}")
    public ResponseEntity<ResponcePaginatedList<Quiz>> getUserQuizzes(@PathVariable String userSearch,
                                                                      @PathVariable int pageSize,
                                                                      @PathVariable int pageNumber,
                                                                      @PathVariable int userId,
                                                                      @RequestParam(defaultValue = "", required = false, value = "sort") String sort) {
        List<Quiz> quizzes = quizService.filterQuizzesByUserId(userSearch, userId, sort);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, pageSize, pageNumber), quizzes.size()));
    }

    @GetMapping("/myfavorite/{userId}/{pageSize}/{pageNumber}")
    public ResponseEntity<ResponcePaginatedList<QuizDto>> getFavoriteQuizzes(@PathVariable int userId,
                                                                          @PathVariable int pageSize,
                                                                          @PathVariable int pageNumber) {
        List<QuizDto> quizzes = quizService.findFavoriteQuizzes(userId);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, pageSize, pageNumber), quizzes.size()));
    }

    @GetMapping("/myfavorite/{userSearch}/{userId}/{pageSize}/{pageNumber}")
    public ResponseEntity<ResponcePaginatedList<QuizDto>> getFavoriteQuizzes(@PathVariable String userSearch,
                                                                          @PathVariable int userId,
                                                                          @PathVariable int pageSize,
                                                                          @PathVariable int pageNumber) {
        List<QuizDto> quizzes = quizService.searchInFavoriteQuizzes(userId, userSearch);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, pageSize, pageNumber), quizzes.size()));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ResponseText> getCategoryNameByCategoryId(@PathVariable int categoryId) {
        return ResponseEntity.ok(new ResponseText(quizService.getCategoryNameByCategoryId(categoryId)));
    }

    @PostMapping("/newicon/{userId}")
    public ResponseEntity<String> changeProfileIcon(@RequestParam(value = "image") MultipartFile image, @PathVariable int userId) {
        boolean isRecordAffected = userRepo.updateProfileImage(storeFileService.uploadToLocalFileSystem(image), userId);

        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/getimage/{userId}")
    public ResponseEntity<ResponseText> getUserImage(@PathVariable int userId) {
        return ResponseEntity.ok(new ResponseText(userRepo.getImageByUserId(userId)));
    }

    @PostMapping("/status/{userId}")
    public ResponseEntity<String> updateNotificationStatus(@RequestBody String status, @PathVariable int userId) {
        boolean isRecordAffected = userRepo.changeNotificationStatus(status, userId);

        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<NotificationStatus> getUserNotificationStatus(@PathVariable int userId){
        return ResponseEntity.ok(userRepo.getNotificationStatus(userId));
    }

    @DeleteMapping("/delete/{id}")
    void deleteUserById(@PathVariable int id) {
        userRepo.deleteUserById(id);
    }

    @PostMapping("updateActive/{userId}")
    public ResponseEntity<String> updateStatus(@RequestBody String status, @PathVariable int userId) {
        boolean isRecordAffected = userRepo.updateStatusById(userId);

        if (isRecordAffected) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/played/{pageSize}/{pageNumber}/{userId}")
    public ResponcePaginatedList<GameDto> getPlayedGames(@PathVariable int pageSize,
                                                         @PathVariable int pageNumber,
                                                         @PathVariable int userId,
                                                         @RequestParam(defaultValue = "", required = false, value = "sort") String sort,
                                                         @RequestParam(defaultValue = "", required = false, value = "search") String search) {
        if (search.isEmpty()) {
            return new ResponcePaginatedList<>(gameDao.getPlayedGame(userId, pageSize, pageNumber, sort), gameDao.getNumberOfRecord(userId));
        }
        return new ResponcePaginatedList<>(gameDao.getFilteredPlayedGame(userId, pageSize, pageNumber, sort, search), gameDao.getNumberOfRecord(userId));
    }

    @GetMapping("/gameresult/{gameId}")
    public ResponseEntity<Set<Player>> getGameResult(@PathVariable int gameId) {
        return ResponseEntity.ok(gameDao.getGameResult(gameId));
    }

    @GetMapping("/reject/{pageSize}/{pageNumber}/{userId}")
    public ResponseEntity<ResponcePaginatedList<Quiz>> getUserRejectedQuizzes(@PathVariable int pageSize,
                                                                              @PathVariable int pageNumber,
                                                                              @PathVariable int userId,
                                                                              @RequestParam(defaultValue = "", required = false, value = "sort") String sort) {
        List<Quiz> quizzes = quizService.getRejectedQuizzesByUserId(userId, sort);
        return ResponseEntity.ok(new ResponcePaginatedList<>(paginationService.paginate(quizzes, pageSize, pageNumber), quizzes.size()));
    }

    @GetMapping("/rejectMessage/{quizId}")
    public ResponseEntity<List<RejectMessage>> getRejectMessage(@PathVariable int quizId) {
        return ResponseEntity.ok(quizService.getRejectMessages(quizId));
    }

    @GetMapping("/moderatorQuizzes/{moderatorId}")
    public ResponseEntity<List<QuizDto>> getModeratorAssignment(@PathVariable int moderatorId){
        return ResponseEntity.ok(quizService.getModeratorQuizzes(moderatorId));
    }
}
