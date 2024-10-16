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

    public List<Rating> getAllRating() {
        return jdbcTemplate.query(
                "SELECT rating_id, name FROM ratings_mpa",
                RatingDbStorage::mapRow
        );
    }

    public Rating getRatingById(Integer ratingId) {
        if (ratingId != null) {
            return jdbcTemplate.queryForObject(
                    "SELECT rating_id, nameFROM ratings_mpa WHERE rating_id = ?",
                    RatingDbStorage::mapRow,
                    ratingId
            );
        }
        throw new NotFoundException("id " + ratingId + " не найден");
    }

    private static Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Rating(
                rs.getInt("rating_id"),
                rs.getString("name"),
                rs.getString("description"));
    }
}
