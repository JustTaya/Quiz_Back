package com.quiz.dao;

import com.quiz.dao.mapper.GameSessionMapper;
import com.quiz.dto.GameDto;
import com.quiz.dto.GameSessionDto;
import com.quiz.entities.Game;
import com.quiz.entities.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;


@RequiredArgsConstructor
@Repository
public class GameDao {
    private final JdbcTemplate jdbcTemplate;

    private final static String LIMIT_OFFSET = " LIMIT ? OFFSET ? ";
    private final static String INSERT_GAME = "INSERT INTO games (quiz_id, host_id, question_timer, date, max_users_number) VALUES (?, ?, ?, ?, ?)";
    private final static String GET_PLAYER_LIMIT = "SELECT max_users_number FROM games WHERE id =?";
    private final static String SAVE_SCORE = "INSERT INTO score (user_id, game_id, score) VALUES (?, ?, ?)";

    private static final String GET_GAME = "SELECT quiz_id, host_id, question_timer,max_users_number FROM games WHERE id = ?";
    private static final String GET_GAMES_BY_USER_ID = "SELECT games.id, quizzes.name, games.date, score FROM score " +
            "INNER JOIN games ON score.game_id = games.id " +
            "INNER JOIN quizzes ON quizzes.id = games.quiz_id " +
            "WHERE user_id = ?";
    private static final String COUNT_NUMBER_OF_PLAYED_GAMES = "SELECT COUNT(*) FROM score " +
            "INNER JOIN games ON score.game_id = games.id " +
            "INNER JOIN quizzes ON quizzes.id = games.quiz_id " +
            "WHERE user_id = ? ";
    private static final String GET_GAME_RESULT_BY_ID = "SELECT users.name || ' ' || users.surname AS name, score " +
            "FROM score INNER JOIN users " +
            "ON users.id = user_id " +
            "WHERE game_id = ? ";

    public int insertGame(int quizId, int hostId, int questionTimer, int max_users_number) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_GAME, new String[]{"id"});
                    ps.setInt(1, quizId);
                    ps.setInt(2, hostId);
                    ps.setInt(3, questionTimer);
                    ps.setDate(4, Date.valueOf(LocalDate.now()));
                    ps.setInt(5, max_users_number);
                    return ps;
                }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).intValue();
    }

    public int getUserNumberByGameId(int gameId) {
        return jdbcTemplate.query(GET_PLAYER_LIMIT,
                new Object[]{gameId},
                (resultSet, i) -> resultSet.getInt("max_users_number")).get(0);
    }

    public void saveScore(int userId, int gameId, int score) {
        jdbcTemplate.update(SAVE_SCORE, userId, gameId, score);
    }

    public GameSessionDto getGame(int gameId) {
        return jdbcTemplate.queryForObject(GET_GAME, new Object[]{gameId}, new GameSessionMapper());
    }

    public List<GameDto> getPlayedGame(int userId, int pageSize, int pageNumber, String sort) {
        List<GameDto> gameDtos = jdbcTemplate.query(
                sort.isEmpty() ? GET_GAMES_BY_USER_ID + LIMIT_OFFSET : GET_GAMES_BY_USER_ID + " ORDER BY " + sort + LIMIT_OFFSET,
                new Object[]{userId, pageSize, pageSize * pageNumber},
                ((resultSet, i) -> new GameDto(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getDate("date"),
                        resultSet.getInt("score"))));

        if (gameDtos.isEmpty()) {
            return null;
        }

        return gameDtos;
    }

    public int getNumberOfRecord(int userId) {
        return jdbcTemplate.queryForObject(COUNT_NUMBER_OF_PLAYED_GAMES,
                new Object[]{userId},
                (resultSet, i) -> resultSet.getInt("count"));
    }

    public Set<Player> getGameResult(int gameId) {
        return new TreeSet<>(jdbcTemplate.query(GET_GAME_RESULT_BY_ID, new Object[]{gameId},
                ((resultSet, i) -> new Player(resultSet.getInt("score"),
                        resultSet.getString("name")
                ))));
    }
}
