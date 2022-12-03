package com.quiz.dao;

import com.quiz.dao.mapper.TagMapper;
import com.quiz.entities.Tag;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TagDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String TAG_BY_ID = "SELECT id, name FROM tags WHERE id = ?";
    private static final String TAG_BY_NAME = "SELECT id, name FROM tags WHERE name = ?";
    private static final String TAGS_BY_NAME = "SELECT id, name FROM tags WHERE name SIMILAR TO ?";
    private static final String TAGS_BY_QUIZ = "SELECT id, name FROM tags INNER JOIN quizzes_tags on id = tag_id where quiz_id = ?";
    private static final String INSERT_TAG = "INSERT INTO tags (name) SELECT (?) WHERE NOT EXISTS(SELECT FROM tags WHERE name = ?)";

    public Tag getTagById(int id) {
        List<Tag> tags;

        try {
            tags = jdbcTemplate.query(
                    TAG_BY_ID,
                    new Object[]{id},
                    new TagMapper()
            );
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find tag by id '%s' database error occurred", id));
        }

        return tags.get(0);
    }

    public Tag getTagByName(String name) {
        List<Tag> tags;

        try {
            tags = jdbcTemplate.query(
                    TAG_BY_NAME,
                    new Object[]{name},
                    new TagMapper()
            );
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find tag by name '%s' database error occurred", name));
        }

        return tags.get(0);
    }

    public List<Tag> getTagsByName(String name) {
        List<Tag> tags;

        try {
            tags = jdbcTemplate.query(
                    TAGS_BY_NAME,
                    new Object[]{name},
                    new TagMapper()
            );
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find tag by name '%s' database error occurred", name));
        }

        return tags;
    }

    public List<Tag> getTagsByQuiz(int quizId) {
        List<Tag> tagsByQuiz = jdbcTemplate.query(TAGS_BY_QUIZ, new Object[]{quizId}, new TagMapper());
        if (tagsByQuiz.isEmpty()) {
            return null;
        }
        return tagsByQuiz;
    }

    public Tag insert(Tag entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(INSERT_TAG, new String[]{"id"});
                ps.setString(1, entity.getName());
                ps.setString(2, entity.getName());
                return ps;
            }, keyHolder);
        } catch (DataAccessException e) {
            throw new DatabaseException("Database access exception while tag insert");
        }

        Tag tag = new Tag();
        if (keyHolder.getKey() == null) {
            tag.setId(getTagByName(entity.getName()).getId());
        } else {
            tag.setId(keyHolder.getKey().intValue());
        }

        return tag;
    }
}
