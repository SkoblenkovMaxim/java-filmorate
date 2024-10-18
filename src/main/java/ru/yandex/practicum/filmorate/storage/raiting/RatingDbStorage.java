package ru.yandex.practicum.filmorate.storage.raiting;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.rating.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
public class RatingDbStorage implements RatingStorage {

    private final JdbcTemplate jdbcTemplate;

    @SuppressWarnings("all")
    public List<Rating> getAllRating() {
        return jdbcTemplate.query(
                "SELECT * FROM ratings_mpa",
                RatingDbStorage::mapRow
        );
    }

    @SuppressWarnings("all")
    public Rating getRatingById(Integer ratingId) {
        if (ratingId != null) {
            return jdbcTemplate.query(
                    "SELECT * FROM ratings_mpa WHERE rating_id = ?",
                    rs -> rs.next() ? mapRow(rs, 1) : null,
                    ratingId
            );
        }
        throw new NotFoundException("id рейтинга не должен быть null");
    }

    private static Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Rating(
                rs.getInt("rating_id"),
                rs.getString("name"),
                rs.getString("description"));
    }
}
