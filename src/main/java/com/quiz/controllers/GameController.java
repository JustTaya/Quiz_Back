package com.quiz.controllers;

import com.quiz.dto.GameAnswersDto;
import com.quiz.dto.GameQuestionsDto;
import com.quiz.dto.GameSessionDto;
import com.quiz.entities.Player;
import com.quiz.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate template;

    @PostMapping("/play/addSession")
    public int addGameSession(@RequestBody GameSessionDto gameSessionDto) {
        return gameService.addGameSession(gameSessionDto.getQuizId(), gameSessionDto.getHostId(),
                gameSessionDto.getQuestionTimer(), gameSessionDto.getMaxUsersNumber());
    }

    @MessageMapping("/play/game/{gameId}/user")
    public void userJoinGameSession(@DestinationVariable int gameId, @RequestBody Player player, SimpMessageHeaderAccessor headerAccessor) {
        template.convertAndSend("/play/game/" + gameId, gameService.addUserInSession(gameId, player));
    }

    @MessageMapping("/play/game/{gameId}/start")
    public void startGame(@DestinationVariable int gameId) {
        this.sendQuestion(gameId, this.gameService.nextQuestion(gameId));
    }

    @MessageMapping("play/game/{gameId}/sendAnswer")
    public void receiveAnswer(@DestinationVariable int gameId, @RequestBody GameAnswersDto answers) {
        if (this.gameService.handleAnswer(gameId, answers.getPlayer(), answers)) {
            this.sendQuestion(gameId, this.gameService.nextQuestion(gameId));
        }
    }

    @MessageMapping("/play/game/{gameId}/finish")
    public void finishGame(@DestinationVariable int gameId) {
        GameSessionDto rating = gameService.deleteGameSession(gameId);
        if (rating != null) {
            template.convertAndSend("/play/game/" + gameId, rating);
        }
    }

    private void sendQuestion(int gameId, GameQuestionsDto gameQuestionsDto) {
        template.convertAndSend("/play/game/" + gameId, gameQuestionsDto);
    }

    public void handleUserDisconnection(int userId, int gameId) {
        this.gameService.onUserDisconnection(userId, gameId);
    }
}
