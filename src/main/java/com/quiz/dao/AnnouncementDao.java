package com.quiz.dao;

import com.quiz.dao.mapper.AnnouncementMapper;
import com.quiz.entities.Announcement;
import com.quiz.entities.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AnnouncementDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String GET_ANNON_FOR_AUTH_BY_ID = "SELECT date, text, generated FROM system_announcements " +
            "INNER JOIN friends ON sender_id = friend_id " +
            "WHERE user_id = ? AND sender_id <> ?" +
            "ORDER BY date desc";
    private static final String GET_ANNON_FOR_ANONIM = "SELECT date, text, generated FROM system_announcements " +
            "WHERE generated = false " +
            "ORDER BY date desc";
    private static final String CREATE_GAME_RESULT_ANNON = "INSERT INTO system_announcements (sender_id, date, text, generated) VALUES (?, ?, ?, ?)";

    public List<Announcement> getAnnouncementsByUserId(int userId) {
        return jdbcTemplate.query(GET_ANNON_FOR_AUTH_BY_ID,
                new Object[]{userId, userId}, new AnnouncementMapper());
    }

    public List<Announcement> getAnnouncements() {
        return jdbcTemplate.query(GET_ANNON_FOR_ANONIM,
                new AnnouncementMapper());
    }

    public void generateGameResultAnnouncement(Player player, int gamePlace) {
        jdbcTemplate.update(CREATE_GAME_RESULT_ANNON, player.getUserId(),
                Date.valueOf(LocalDate.now()),
                "Friend " + player.getUserName() + " finish on " + (++gamePlace) + " place and collect " + player.getUserScore() + " points for game",
                true);
    }
}
