package com.quiz.dao.mapper;

import com.quiz.entities.Announcement;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AnnouncementMapper implements RowMapper<Announcement> {

    public static final String DATE = "date";
    public static final String TEXT = "text";
    public static final String GENERATED = "generated";
    @Override
    public Announcement mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Announcement(resultSet.getDate(DATE), resultSet.getString(TEXT), resultSet.getBoolean(GENERATED));
    }
}
