package com.quiz.dto;

import com.quiz.entities.Answer;
import com.quiz.entities.Player;
import lombok.Data;

import java.util.List;

@Data
public class GameAnswersDto {
    private List<Answer> answers;
    private Player player;
}
