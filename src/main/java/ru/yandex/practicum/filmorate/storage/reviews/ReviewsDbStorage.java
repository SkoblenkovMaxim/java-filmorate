package ru.yandex.practicum.filmorate.storage.reviews;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.reviews.Reviews;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
public class ReviewsDbStorage implements ReviewsStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String QUERY_TO_SET_USEFUL = "UPDATE reviews SET useful = ((SELECT COUNT(review_id) " +
            "FROM reviews_likes WHERE review_id = ?) - (SELECT COUNT(review_id) FROM reviews_dislikes " +
            "WHERE review_id = ?)) WHERE review_id = ?;";

    @Override
    public Reviews addReviews(Reviews reviews) {

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)",
                    new String[]{"review_id"});
            ps.setString(1, reviews.getContent());
            ps.setBoolean(2, reviews.getIsPositive());
            ps.setLong(3, reviews.getUserId());
            ps.setLong(4, reviews.getFilmId());
            ps.setLong(5, reviews.getUseful());
            return ps;
        }, keyHolder);

        var key = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return Reviews.builder()
                .reviewId(key)
                .content(reviews.getContent())
                .isPositive(reviews.getIsPositive())
                .userId(reviews.getUserId())
                .filmId(reviews.getFilmId())
                .useful(reviews.getUseful())
                .build();
    }

    @Override
    public Reviews updateReviews(Reviews reviews) {
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?");
            stmt.setString(1, reviews.getContent());
            stmt.setBoolean(2, reviews.getIsPositive());
            stmt.setLong(3, reviews.getReviewId());
            return stmt;
        });
        return Reviews.builder()
                .reviewId(reviews.getReviewId())
                .content(reviews.getContent())
                .isPositive(reviews.getIsPositive())
                .userId(reviews.getUserId())
                .filmId(reviews.getFilmId())
                .build();
    }

    @Override
    public void deleteReviews(long idReviews) {
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM reviews WHERE review_id = ?");
            stmt.setLong(1, idReviews);
            return stmt;
        });
    }

    @Override
    public Reviews getReviews(long idReviews) {
        return jdbcTemplate.query("SELECT * FROM reviews WHERE review_id = ?",
                new Object[]{idReviews},
                        ReviewsDbStorage::mapRowReviews)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ValidationException("Отзыв с id " + idReviews + " не найден"));
    }

    @Override
    public List<Reviews> getReviewsByFilmId(Long idFilm, Long count) {

        if (idFilm == null || idFilm.describeConstable().isEmpty()) {
            return jdbcTemplate.query("SELECT * FROM reviews",
                    ReviewsDbStorage::mapRowReviews,
                    idFilm);
        } else if (count.describeConstable().isEmpty()) {
            return jdbcTemplate.query("SELECT * FROM reviews WHERE film_id = ? LIMIT 10",
                    new Object[]{idFilm},
                    ReviewsDbStorage::mapRowReviews);
        } else {
            return jdbcTemplate.query("SELECT * FROM reviews WHERE film_id = ?",
                    new Object[]{idFilm, count},
                    ReviewsDbStorage::mapRowReviews);
        }
    }

    @Override
    public void addLike(Long idReviews, Long idUser) {
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO reviews_likes (review_id, user_id) VALUES (?, ?)");
            stmt.setLong(1, idReviews);
            stmt.setLong(2, idUser);
            return stmt;
        });
        setUsefulScore(idReviews);
    }

    @Override
    public void addDislike(Long idReviews, Long idUser) {
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO reviews_dislikes (review_id, user_id) VALUES (?, ?)");
            stmt.setLong(1, idReviews);
            stmt.setLong(2, idUser);
            return stmt;
        });
        setUsefulScore(idReviews);
    }

    @Override
    public void deleteLike(Long idReviews, Long idUser) {
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM reviews_likes WHERE review_id = ? AND user_id = ?");
            stmt.setLong(1, idReviews);
            stmt.setLong(2, idUser);
            return stmt;
        });
        setUsefulScore(idReviews);
    }

    @Override
    public void deleteDislike(Long idReviews, Long idUser) {
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(
                    "DELETE FROM reviews_dislikes WHERE review_id = ? AND user_id = ?");
            stmt.setLong(1, idReviews);
            stmt.setLong(2, idUser);
            return stmt;
        });
        setUsefulScore(idReviews);
    }

    public void setUsefulScore(Long idReviews) {

        jdbcTemplate.update(QUERY_TO_SET_USEFUL, idReviews, idReviews);
    }

    private static Reviews mapRowReviews(ResultSet rs, int rowNum) throws SQLException {
        return Reviews.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .build();
    }
}
