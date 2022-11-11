package com.quiz.dao.mapper;

import com.quiz.dto.GameSessionDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GameSessionMapper implements RowMapper<GameSessionDto> {
    public static final String GAME_QUIZ_ID = "quiz_id";
    public static final String GAME_HOST_ID = "host_id";
    public static final String GAME_QUESTION_TIMER = "question_timer";
    public static final String GAME_MAX_USERS_NUMBER = "max_users_number";

    @Override
    public GameSessionDto mapRow(ResultSet resultSet, int i) throws SQLException {
        GameSessionDto game = new GameSessionDto();

        game.setQuizId(resultSet.getInt(GAME_QUIZ_ID));
        game.setHostId(resultSet.getInt(GAME_HOST_ID));
        game.setQuestionTimer(resultSet.getInt(GAME_QUESTION_TIMER));
        game.setMaxUsersNumber(resultSet.getInt(GAME_MAX_USERS_NUMBER));

        return game;
    }
}
