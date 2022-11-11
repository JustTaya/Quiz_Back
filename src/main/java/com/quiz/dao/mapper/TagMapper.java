package com.quiz.dao.mapper;

import com.quiz.entities.Tag;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class TagMapper implements RowMapper<Tag> {
    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";


    @Override
    public Tag mapRow(ResultSet resultSet, int i) throws SQLException {
        Tag tag = new Tag();

        tag.setId(resultSet.getInt(TAG_ID));
        tag.setName(resultSet.getString(TAG_NAME));

        return tag;
    }
}
