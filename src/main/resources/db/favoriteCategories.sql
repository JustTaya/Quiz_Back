CREATE OR REPLACE FUNCTION favorite_categories(int)
    RETURNS TABLE
            (
                category_id int,
                count_games bigint
            )
AS
$BODY$
BEGIN
    RETURN QUERY EXECUTE '
SELECT category_id, COUNT(games.id) as count_games
FROM quizzes
         INNER JOIN games ON quizzes.id = games.quiz_id
WHERE games.id IN (
    SELECT game_id
    FROM score
    WHERE score.user_id = $1
)
GROUP BY category_id
ORDER BY count_games;'
        USING $1;
    RETURN;
END
$BODY$
    LANGUAGE plpgsql;