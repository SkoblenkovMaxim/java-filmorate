package ru.yandex.practicum.filmorate.storage.like;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.like.Like;

@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    @SuppressWarnings("all")
    private static final String CREATE_QUERY = """
            INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)
            """;

    @SuppressWarnings("all")
    private static final String GET_ALL_QUERY = """
            SELECT * FROM film_likes
            """;

    @SuppressWarnings("all")
    private static final String DELETE_QUERY = """
            DELETE FROM film_likes WHERE film_id = ? AND user_id = ?
            """;

    @SuppressWarnings("all")
    private static final String GET_LIKES_COUNT_QUERY = """
            SELECT COUNT(*) FROM film_likes WHERE film_id = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createLike(Like like) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_QUERY,
                    new String[]{"like_id"});
            ps.setLong(1, like.getFilmId());
            ps.setLong(2, like.getUserId());
            return ps;
        });
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(DELETE_QUERY);
            ps.setLong(1, filmId);
            ps.setLong(2, userId);
            return ps;
        });
    }

    @Override
    public List<Like> getAllLikes() {
        return jdbcTemplate.query(
                GET_ALL_QUERY,
                LikeDbStorage::resultSetToLike
        );
    }

    @Override
    public int getLikesCount(Long filmId) {
        Integer count = jdbcTemplate.queryForObject(
                GET_LIKES_COUNT_QUERY,
                Integer.class,
                filmId
        );
        return count != null ? count : 0;
    }

    private static Like resultSetToLike(ResultSet rs, int i) throws SQLException {
        return Like.builder()
                .id(rs.getLong("like_id"))
                .filmId(rs.getLong("film_id"))
                .userId(rs.getLong("user_id"))
                .build();
    }
}
