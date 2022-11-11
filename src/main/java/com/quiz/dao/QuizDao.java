package com.quiz.dao;

import com.quiz.dao.mapper.QuizMapper;
import com.quiz.dto.QuizDto;
import com.quiz.entities.Quiz;
import com.quiz.entities.StatusType;
import com.quiz.exceptions.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.sql.PreparedStatement;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.quiz.dao.mapper.QuizMapper.*;


@Repository
@RequiredArgsConstructor
public class QuizDao {

    private final JdbcTemplate jdbcTemplate;

    private final static String GET_QUIZZES_BY_STATUS = "SELECT * FROM quizzes WHERE status = ?::status_type";
    private final static String GET_ALL_QUIZZES = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category FROM quizzes INNER JOIN categories ON categories.id = category_id WHERE quizzes.status = 'ACTIVE'";
    private final static String GET_QUIZ_BY_ID = "SELECT * FROM quizzes WHERE id = ?";
    private final static String GET_QUIZZES_CREATED_BY_USER_ID = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category FROM quizzes INNER JOIN categories ON categories.id = category_id WHERE author = ? AND (status<>'DELETED' AND status<>'DEACTIVATED')";
    private final static String GET_FAVORITE_QUIZZES_BY_USER_ID = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category FROM quizzes INNER JOIN categories ON categories.id = category_id INNER JOIN favorite_quizzes ON quizzes.id = quiz_id WHERE user_id = ?";
    private final static String GET_QUIZZES_BY_CATEGORY_ID = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category FROM quizzes INNER JOIN categories ON categories.id = category_id WHERE (category_id = ?) AND (quizzes.status = 'ACTIVE')";
    private final static String GET_QUIZZES_BY_TAG = "SELECT * FROM quizzes INNER JOIN quizzes_tags on id = quiz_id where tag_id = ?";
    private final static String GET_QUIZZES_BY_NAME = "SELECT * FROM quizzes WHERE name LIKE ?";
    private final static String GET_QUIZ_IMAGE_BY_QUIZ_ID = "SELECT image FROM quizzes WHERE id = ?";
    private final static String INSERT_QUIZ = "INSERT INTO quizzes (name , author, category_id, date, description,status, modification_time) VALUES (?,?,?,?,?,?::status_type,?)";
    private final static String ADD_TAG_TO_QUIZ = "INSERT INTO quizzes_tags (quiz_id, tag_id) VALUES (?,?)";
    private final static String UPDATE_QUIZ = "UPDATE quizzes SET name = ?, author = ?, category_id = ?, date = ?, description = ?, status = ?::status_type, modification_time = ? WHERE id = ?";
    private final static String UPDATE_QUIZ_IMAGE = "UPDATE quizzes SET image = ? WHERE id = ?";
    private final static String GET_FILTERED_QUIZZES = "SELECT quizzes.id, quizzes.name, quizzes.image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category, users.name AS authorName, users.surname AS authorSurname FROM quizzes INNER JOIN categories ON categories.id = category_id INNER JOIN users ON quizzes.author = users.id WHERE quizzes.name ~* ? OR categories.name ~* ? OR CONCAT(users.name, ' ', surname) ~*? OR date::text ~* ?";
    private final static String GET_POPULAR_QUIZ = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category, COUNT(quiz_id)  AS counter FROM quizzes INNER JOIN categories ON categories.id = category_id INNER JOIN favorite_quizzes ON quizzes.id = favorite_quizzes.quiz_id WHERE quizzes.status = 'ACTIVE' GROUP BY quizzes.id, categories.id ORDER BY counter DESC LIMIT ?";
    private final static String FILTER_QUIZZES_CREATED_BY_USER = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category FROM quizzes INNER JOIN categories ON categories.id = category_id WHERE author = ? AND (status<>'DELETED' AND status<>'DEACTIVATED') AND (quizzes.name ~* ? OR categories.name ~* ? OR date::text ~* ?)";
    private final static String FILTER_FAVORITE_QUIZZES = "SELECT quizzes.id, quizzes.name, image, author, category_id, date, description, status, modification_time, categories.id, categories.name AS category FROM quizzes INNER JOIN categories ON categories.id = category_id INNER JOIN favorite_quizzes ON quizzes.id = quiz_id WHERE user_id = ? AND (quizzes.name ~* ? OR categories.name ~* ? OR CONCAT(name, ' ', surname) ~*? OR date::text ~* ?)";

    private final static String IS_FAVORITE_QUIZ = "select * from favorite_quizzes WHERE quiz_id = ? AND user_id = ?";
    private final static String MARK_QUIZ_AS_FAVORITE = "INSERT INTO favorite_quizzes (user_id, quiz_id) VALUES(?, ?) ";
    private final static String UNMARK_QUIZ_AS_FAVORITE = "DELETE FROM favorite_quizzes where user_id = ? AND quiz_id = ?";
    private final static String GET_TAGS_BY_QUIZ_Id = "select name from tags INNER JOIN quizzes_tags ON tags.id = quizzes_tags.tag_id WHERE quizzes_tags.quiz_id = ?";

    private static final String GET_QUIZ_RECOMMENDATIONS = "SELECT quizzes.id, quizzes.name, quizzes.image, quizzes.author, quizzes.category_id, quizzes.date,quizzes.description, quizzes.status, quizzes.modification_time, categories.name AS category, COUNT(games.id) AS count_games_general, count_games FROM quizzes INNER JOIN games ON quizzes.id = games.quiz_id INNER JOIN favorite_categories(?) ON quizzes.category_id=favorite_categories.category_id INNER JOIN categories ON quizzes.category_id = categories.id WHERE quizzes.status='ACTIVE' GROUP BY quizzes.id,favorite_categories.count_games, categories.name ORDER BY count_games_general DESC , count_games DESC LIMIT ?";
    private static final String GET_QUIZ_RECOMMENDATIONS_BY_FRIENDS = "SELECT quizzes.id, quizzes.name, quizzes.author, quizzes.category_id, quizzes.date, quizzes.description, quizzes.status, quizzes.modification_time, COUNT(games.id) AS gamescount FROM games INNER JOIN quizzes on games.quiz_id=quizzes.id WHERE games.id IN(SELECT games.quiz_id FROM games WHERE games.id IN (SELECT games.id FROM score WHERE score.user_id IN (SELECT friend_id FROM friends WHERE friends.user_id = ?) ) ) AND quizzes.status = 'ACTIVE' GROUP BY quizzes.id ORDER BY gamescount DESC LIMIT ?";


    //Functionality for dashboard
    private static final String GET_TOP_POPULAR_QUIZZES = "SELECT quizzes.id, quizzes.name, quizzes.author, quizzes.category_id, quizzes.date, quizzes.description, quizzes.status, quizzes.modification_time, COUNT(games.id) AS gamescount FROM games INNER JOIN quizzes ON games.quiz_id = quizzes.id GROUP BY quizzes.id ORDER BY gamescount DESC LIMIT ?";
    private static final String GET_TOP_POPULAR_QUIZZES_BY_CATEGORY = "SELECT quizzes.id, quizzes.name, quizzes.author, quizzes.category_id, quizzes.date, quizzes.description, quizzes.status, quizzes.modification_time, COUNT(games.id) AS gamescount FROM games INNER JOIN quizzes ON games.quiz_id = quizzes.id WHERE category_id=? GROUP BY quizzes.id ORDER BY gamescount DESC LIMIT ?";
    private static final String GET_RECENT_GAMES = "SELECT quizzes.id, quizzes.name, quizzes.author, quizzes.category_id, quizzes.date, quizzes.description, quizzes.status, quizzes.modification_time FROM games INNER JOIN quizzes ON games.quiz_id = quizzes.id WHERE games.id IN (SELECT games.id FROM score WHERE user_id = ?) AND games.status = 'FINISHED' GROUP BY quizzes.id, games.date ORDER BY games.date DESC LIMIT ?";

    public static final String TABLE_QUIZZES = "quizzes";
    private final static String GET_GAMES_CREATED_BY_USER_ID = "SELECT * FROM quizzes WHERE author = ?";
    private final static String GET_FAVORITE_GAMES_BY_USER_ID = "SELECT * FROM quizzes INNER JOIN favorite_quizzes ON id = quiz_id WHERE user_id = ?";
    private final static String GET_QUIZ_CATEGORY_BY_CATEGORY_ID = "SELECT name FROM categories WHERE id = ?";
    private final static String GET_QUIZZES_BY_STATUS_NAME = "SELECT sq.qid qid,sq.qauthor qauthor, u.id uid, u.name uname, u.surname usurname, u.email uemail, sq.qdate qdate,sq.qdescription qdescription,sq.qimage qimage, sq.qmodificationtime qmodificationtime, sq.qname qname, sq.cname cname, sq.qcategoryid qcategoryid, sq.qstatus qstatus FROM (SELECT q.id qid,q.author qauthor,q.date qdate,q.description qdescription,q.image qimage, q.modification_time qmodificationtime, q.name qname, q.category_id qcategoryid, q.status qstatus, c.name cname FROM quizzes q INNER JOIN categories c on q.category_id = c.id where q.status = ?::status_type) sq INNER JOIN users u on sq.qauthor = u.id";
    private final static String GET_QUIZ_BY_ID_NAME = "SELECT sq.qid qid,sq.qauthor qauthor, u.id uid, u.name uname, u.surname usurname, u.email uemail, sq.qdate qdate,sq.qdescription qdescription,sq.qimage qimage, sq.qmodificationtime qmodificationtime, sq.qname qname, sq.cname cname, sq.qcategoryid qcategoryid, sq.qstatus qstatus FROM (SELECT q.id qid,q.author qauthor,q.date qdate,q.description qdescription,q.image qimage, q.modification_time qmodificationtime, q.name qname, q.category_id qcategoryid, q.status qstatus, c.name cname FROM quizzes q INNER JOIN categories c on q.category_id = c.id where q.id = ?) sq INNER JOIN users u on sq.qauthor = u.id";


    public List<Quiz> getGamesCreatedByUser(int userId) {

        List<Quiz> quizzesCreatedByUser = jdbcTemplate.query(GET_GAMES_CREATED_BY_USER_ID, new Object[]{userId}, new QuizMapper());

        if (quizzesCreatedByUser.isEmpty()) {
            return null;
        }

        return quizzesCreatedByUser;
    }

    public List<QuizDto> getQuizzesByStatus(StatusType status) {

        List<QuizDto> quizDtos = jdbcTemplate.query(
                GET_QUIZZES_BY_STATUS_NAME,
                new Object[]{status.toString()}, (resultSet, i) -> {
                    QuizDto quiz = new QuizDto();
                    quiz.setName(resultSet.getString("qname"));
                    quiz.setCategoryId(resultSet.getInt("qcategoryid"));
                    quiz.setStatus(StatusType.valueOf(resultSet.getString("qstatus")));
                    quiz.setCategory(resultSet.getString("cname"));
                    quiz.setId(resultSet.getInt("qid"));
                    quiz.setAuthor(resultSet.getInt("qauthor"));
                    quiz.setAuthorName(resultSet.getString("uname"));
                    quiz.setAuthorSurname(resultSet.getString("usurname"));
                    quiz.setAuthorEmail(resultSet.getString("uemail"));
                    quiz.setDate(resultSet.getDate("qdate"));
                    quiz.setDescription(resultSet.getString("qdescription"));
                    quiz.setModificationTime(resultSet.getTimestamp("qmodificationtime"));

                    return quiz;
                });

        if (quizDtos.isEmpty()) {
            return null;
        }

        return quizDtos;
    }

    public List<Quiz> getAllQuizzes(int userId) {
        List<Quiz> quizzes = jdbcTemplate.query(GET_ALL_QUIZZES, new QuizMapper());

        if (quizzes.isEmpty()) {
            return null;
        }

        if (userId == 0) {
            quizzes.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
        } else {
            quizzes.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
            quizzes.forEach(quiz -> quiz.setFavorite(isQuizFavorite(quiz.getId(), userId)));
        }

        return quizzes;
    }

    public QuizDto findById(int id) {
        List<QuizDto> quizzes;

        try {
            quizzes = jdbcTemplate.query(
                    GET_QUIZ_BY_ID_NAME,
                    new Object[]{id}, (resultSet, i) -> {
                        QuizDto quiz = new QuizDto();
                        quiz.setName(resultSet.getString("qname"));
                        quiz.setCategoryId(resultSet.getInt("qcategoryid"));
                        quiz.setStatus(StatusType.valueOf(resultSet.getString("qstatus")));
                        quiz.setCategory(resultSet.getString("cname"));
                        quiz.setId(resultSet.getInt("qid"));
                        quiz.setAuthor(resultSet.getInt("qauthor"));
                        quiz.setAuthorName(resultSet.getString("uname"));
                        quiz.setAuthorSurname(resultSet.getString("usurname"));
                        quiz.setAuthorEmail(resultSet.getString("uemail"));
                        quiz.setDate(resultSet.getDate("qdate"));
                        quiz.setDescription(resultSet.getString("qdescription"));
                        quiz.setModificationTime(resultSet.getTimestamp("qmodificationtime"));

                        return quiz;
                    }
            );
            if (quizzes.isEmpty()) {
                return null;
            }
        } catch (DataAccessException e) {
            throw new DatabaseException(String.format("Find quiz by id '%s' database error occured", id));
        }

        return quizzes.get(0);
    }

    public List<Quiz> getQuizzesCreatedByUser(int userId, String sort) {

        List<Quiz> quizzesCreatedByUser = jdbcTemplate.query(
                sort.isEmpty() ? GET_QUIZZES_CREATED_BY_USER_ID : GET_QUIZZES_CREATED_BY_USER_ID + "ORDER BY " + sort,
                new Object[]{userId},
                new QuizMapper());

        if (quizzesCreatedByUser.isEmpty()){
            return null;
        }

        return quizzesCreatedByUser;
    }

    public String getCategoryNameByCategoryId(int categoryId) {
        List<String> categoryNames = jdbcTemplate.query(GET_QUIZ_CATEGORY_BY_CATEGORY_ID, new Object[]{categoryId}, (resultSet, i) -> resultSet.getString("name"));

        return categoryNames.get(0);
    }

    public List<Quiz> findQuizzesByName(String name) {

        List<Quiz> quizzesByName = jdbcTemplate.query(GET_QUIZZES_BY_NAME, new Object[]{"%" + name + "%"}, new QuizMapper());

        if (quizzesByName.isEmpty()) {
            return null;
        }

        return quizzesByName;
    }

    public List<Quiz> getFavoriteQuizzesByUserId(int userId) {
        List<Quiz> quizzesFavoriteByUser = jdbcTemplate.query(GET_FAVORITE_QUIZZES_BY_USER_ID, new Object[]{userId}, new QuizMapper());

        if (quizzesFavoriteByUser.isEmpty()) {
            return null;
        }

        quizzesFavoriteByUser.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
        quizzesFavoriteByUser.forEach(quiz -> quiz.setFavorite(isQuizFavorite(quiz.getId(), userId)));

        return quizzesFavoriteByUser;
    }

    public List<Quiz> getQuizzesByCategory(int categoryId, int userId) {

        List<Quiz> quizzesByCategory = jdbcTemplate.query(GET_QUIZZES_BY_CATEGORY_ID, new Object[]{categoryId}, new QuizMapper());

        if (quizzesByCategory.isEmpty()) {
            return null;
        }

        if (userId == 0) {
            quizzesByCategory.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
        } else {
            quizzesByCategory.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
            quizzesByCategory.forEach(quiz -> quiz.setFavorite(isQuizFavorite(quiz.getId(), userId)));
        }


        return quizzesByCategory;
    }

    public List<Quiz> getQuizzesByTag(int tagId) {

        List<Quiz> quizzesByTag = jdbcTemplate.query(GET_QUIZZES_BY_TAG, new Object[]{tagId}, new QuizMapper());

        if (quizzesByTag.isEmpty()) {
            return null;
        }

        return quizzesByTag;
    }

    public byte[] getQuizImageByQuizId(int quizId) {
        List<byte[]> imageBlob = jdbcTemplate.query(
                GET_QUIZ_IMAGE_BY_QUIZ_ID,
                new Object[]{quizId},
                (resultSet, i) -> resultSet.getBytes("image"));
        if (imageBlob.get(0) == null) {
            return null;
        }
        return imageBlob.get(0);
    }

    @Transactional
    public Quiz insert(Quiz entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection
                        .prepareStatement(INSERT_QUIZ, new String[]{"id"});
                ps.setString(1, entity.getName());
                ps.setInt(2, entity.getAuthor());
                ps.setInt(3, entity.getCategoryId());
                ps.setDate(4, entity.getDate());
                ps.setString(5, entity.getDescription());
                ps.setString(6, String.valueOf(entity.getStatus()));
                ps.setTimestamp(7, entity.getModificationTime());
                return ps;
            }, keyHolder);

        } catch (DataAccessException e) {
            throw new DatabaseException("Database access exception while quiz insert");
        }

        entity.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return entity;
    }

    @Transactional
    public boolean addTagToQuiz(int quizId, int tagId) {
        int affectedRowNumber;
        try {
            affectedRowNumber = jdbcTemplate.update(ADD_TAG_TO_QUIZ, quizId, tagId);
        } catch (DataAccessException e) {
            throw new DatabaseException("Database access exception while quiz-tag insert");
        }
        return affectedRowNumber > 0;
    }

    public boolean updateQuiz(Quiz quiz) {
        int affectedRowNumber = jdbcTemplate.update(UPDATE_QUIZ, quiz.getName(),
                quiz.getAuthor(), quiz.getCategoryId(),
                quiz.getDate(), quiz.getDescription(),
                quiz.getStatus(), quiz.getModificationTime(), quiz.getId());

        return affectedRowNumber > 0;
    }

    public boolean updateQuizImage(MultipartFile image, int quizId) {
        int affectedNumberOfRows = 0;
        try {
            affectedNumberOfRows = jdbcTemplate.update(UPDATE_QUIZ_IMAGE, image.getBytes(), quizId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return affectedNumberOfRows > 0;
    }


    public List<Quiz> getTopPopularQuizzes(int limit) {
        List<Quiz> quizzes = jdbcTemplate.query(
                GET_TOP_POPULAR_QUIZZES,
                new Object[]{limit}, (resultSet, i) -> {
                    Quiz quiz = new Quiz();

                    quiz.setId(resultSet.getInt(QUIZ_ID));
                    quiz.setName(resultSet.getString(QUIZ_NAME));
                    quiz.setAuthor(resultSet.getInt(QUIZ_AUTHOR));
                    quiz.setCategoryId(resultSet.getInt("category_id"));
                    quiz.setDate(resultSet.getDate(QUIZ_DATE));
                    quiz.setDescription(resultSet.getString(QUIZ_DESCRIPTION));
                    quiz.setStatus(StatusType.valueOf(resultSet.getString(QUIZ_STATUS)));
                    quiz.setModificationTime(resultSet.getTimestamp(QUIZ_MODIFICATION_TIME));
                    return quiz;
                }
        );
        if (quizzes.isEmpty()) {
            return null;
        }

        return quizzes;
    }

    public List<Quiz> getTopPopularQuizzesByCategory(int categoryId, int limit) {
        List<Quiz> quizzes = jdbcTemplate.query(
                GET_TOP_POPULAR_QUIZZES_BY_CATEGORY,
                new Object[]{categoryId, limit}, (resultSet, i) -> {
                    Quiz quiz = new Quiz();

                    quiz.setId(resultSet.getInt(QUIZ_ID));
                    quiz.setName(resultSet.getString(QUIZ_NAME));
                    quiz.setAuthor(resultSet.getInt(QUIZ_AUTHOR));
                    quiz.setCategoryId(resultSet.getInt(QUIZ_CATEGORY));
                    quiz.setDate(resultSet.getDate(QUIZ_DATE));
                    quiz.setDescription(resultSet.getString(QUIZ_DESCRIPTION));
                    quiz.setStatus(StatusType.valueOf(resultSet.getString(QUIZ_STATUS)));
                    quiz.setModificationTime(resultSet.getTimestamp(QUIZ_MODIFICATION_TIME));
                    return quiz;
                }
        );
        if (quizzes.isEmpty()) {
            return null;
        }

        return quizzes;
    }

    public List<Quiz> getRecentGames(int userId, int limit) {
        List<Quiz> quizzes = jdbcTemplate.query(
                GET_RECENT_GAMES,
                new Object[]{userId, limit}, (resultSet, i) -> {
                    Quiz quiz = new Quiz();

                    quiz.setId(resultSet.getInt(QUIZ_ID));
                    quiz.setName(resultSet.getString(QUIZ_NAME));
                    quiz.setAuthor(resultSet.getInt(QUIZ_AUTHOR));
                    quiz.setCategoryId(resultSet.getInt("category_id"));
                    quiz.setDate(resultSet.getDate(QUIZ_DATE));
                    quiz.setDescription(resultSet.getString(QUIZ_DESCRIPTION));
                    quiz.setStatus(StatusType.valueOf(resultSet.getString(QUIZ_STATUS)));
                    quiz.setModificationTime(resultSet.getTimestamp(QUIZ_MODIFICATION_TIME));
                    return quiz;
                }
        );
        if (quizzes.isEmpty()) {
            return null;
        }

        return quizzes;
    }

    public List<Quiz> getRecommendations(int userId, int limit) {
        List<Quiz> quizzes = jdbcTemplate.query(
                GET_QUIZ_RECOMMENDATIONS,
                new Object[]{userId, limit}, new QuizMapper()
        );
        if (quizzes.isEmpty()) {
            return null;
        }

        return quizzes;
    }

    public List<Quiz> getRecommendationsByFriends(int userId, int limit) {
        List<Quiz> quizzes = jdbcTemplate.query(
                GET_QUIZ_RECOMMENDATIONS_BY_FRIENDS,
                new Object[]{userId, limit}, (resultSet, i) -> {
                    Quiz quiz = new Quiz();

                    quiz.setId(resultSet.getInt(QUIZ_ID));
                    quiz.setName(resultSet.getString(QUIZ_NAME));
                    quiz.setAuthor(resultSet.getInt(QUIZ_AUTHOR));
                    quiz.setCategoryId(resultSet.getInt(QUIZ_CATEGORY));
                    quiz.setDate(resultSet.getDate(QUIZ_DATE));
                    quiz.setDescription(resultSet.getString(QUIZ_DESCRIPTION));
                    quiz.setStatus(StatusType.valueOf(resultSet.getString(QUIZ_STATUS)));
                    quiz.setModificationTime(resultSet.getTimestamp(QUIZ_MODIFICATION_TIME));
                    return quiz;
                }
        );
        if (quizzes.isEmpty()) {
            return null;
        }

        return quizzes;
    }

    public List<Quiz> getQuizzesByFilter(String searchByUser, int userId) {
        List<Quiz> getFilteredQuizzes = jdbcTemplate.query(
                GET_FILTERED_QUIZZES,
                new Object[]{searchByUser, searchByUser, searchByUser, searchByUser},
                new QuizMapper());

        if (getFilteredQuizzes.isEmpty()) {
            return null;
        }
        getFilteredQuizzes = getFilteredQuizzes.stream().distinct().collect(Collectors.toList());
        getFilteredQuizzes.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));
        getFilteredQuizzes.forEach(quiz -> quiz.setFavorite(isQuizFavorite(quiz.getId(), userId)));

        return getFilteredQuizzes;
    }

    public boolean markQuizAsFavorite(int quizId, int userId) {
        int affectedRowNumber = jdbcTemplate.update(MARK_QUIZ_AS_FAVORITE, userId, quizId);

        return affectedRowNumber > 0;
    }

    public boolean unmarkQuizAsFavorite(int quizId, int userId) {
        int affectedRowNumber = jdbcTemplate.update(UNMARK_QUIZ_AS_FAVORITE, userId, quizId);

        return affectedRowNumber > 0;
    }

    private List<String> getQuizTags(int quizId) {
        List<String> tags = jdbcTemplate.query(
                GET_TAGS_BY_QUIZ_Id,
                new Object[]{quizId}, (resultSet, i) -> resultSet.getString("name")
        );
        if (tags.isEmpty()) {
            return null;
        }

        return tags;
    }

    private boolean isQuizFavorite(int quizId, int userId) {
        List<Integer> answer = jdbcTemplate.query(IS_FAVORITE_QUIZ, new Object[]{quizId, userId}, (resultSet, i) -> {
            return resultSet.getInt("quiz_id");
        });

        return !answer.isEmpty();
    }

    public List<Quiz> getPopularQuizzes(int limit) {
        List<Quiz> quizzes = jdbcTemplate.query(
                GET_POPULAR_QUIZ,
                new Object[]{limit}, new QuizMapper());

        if (quizzes.isEmpty()) {
            return null;
        }
        quizzes.forEach(quiz -> quiz.setTags(getQuizTags(quiz.getId())));

        return quizzes;
    }

    public List<Quiz> filterQuizzesByUserId(String userSearch, int userId, String sort) {
        List<Quiz> quizzes = jdbcTemplate.query(
                sort.isEmpty() ? FILTER_QUIZZES_CREATED_BY_USER : FILTER_QUIZZES_CREATED_BY_USER + "ORDER BY " + sort,
                new Object[]{userId, userSearch, userSearch, userSearch},
                new QuizMapper());

        if (quizzes.isEmpty()) {
            return null;
        }
        return quizzes;
    }

    public List<Quiz> searchInFavoriteQuizzes(int userId, String userSearch) {
        List<Quiz> quizzes = jdbcTemplate.query(
                FILTER_FAVORITE_QUIZZES,
                new Object[]{userId, userSearch, userSearch, userSearch, userSearch},
                new QuizMapper()
        );

        if (quizzes.isEmpty()) {
            return null;
        }
        return quizzes;
    }

}

