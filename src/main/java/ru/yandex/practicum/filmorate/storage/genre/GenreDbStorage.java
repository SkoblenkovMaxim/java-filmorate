package ru.yandex.practicum.filmorate.storage.genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.like.Like;
import ru.yandex.practicum.filmorate.model.genre.Genre;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    @SuppressWarnings("all")
    private static final String CREATE_QUERY = """
            INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)
            """;

    @SuppressWarnings("all")
    private static final String GET_ALL_QUERY = """
            SELECT * FROM film_likes
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createGenre(Genre genre) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, new String[]{"film_genres_id"});
            ps.setLong(1, genre.getFilmId());
            ps.setLong(2, genre.getGenreId());
            return ps;
        });
    }

    @Override
    public Genre getGenreById(Integer filmId) {
        return jdbcTemplate.queryForObject(
                "SELECT film_id, genre_id FROM film_genres WHERE film_id = ?",
                GenreDbStorage::mapRow,
                filmId
        );
    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query(GET_ALL_QUERY, GenreDbStorage::mapRow);
    }

    @Override
    public boolean isContains(Integer id) {
        try {
            getGenreById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static Like resultSetToLike(ResultSet rs, int i) throws SQLException {
        return Like.builder()
                .id(rs.getLong("like_id"))
                .filmId(rs.getLong("film_id"))
                .userId(rs.getLong("user_id"))
                .build();
    }

    private static Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getLong("genre_id"))
                .build();
    }
}