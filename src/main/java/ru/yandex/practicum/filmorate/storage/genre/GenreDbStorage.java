package ru.yandex.practicum.filmorate.storage.genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.genre.FilmGenre;
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
            SELECT * FROM genres
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createFilmGenre(FilmGenre filmGenre) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, new String[]{"film_genres_id"});
            ps.setLong(1, filmGenre.getFilmId());
            ps.setLong(2, filmGenre.getGenreId());
            return ps;
        });
    }

    @SuppressWarnings("all")
    @Override
    public Genre getGenreById(Long genreId) {
        return jdbcTemplate.query(
                "SELECT * FROM genres WHERE genre_id = ?",
                rs -> rs.next() ? resultSetToGenre(rs, 1) : null,
                genreId
        );
    }

    @SuppressWarnings("all")
    @Override
    public List<FilmGenre> getFilmGenresByFilmId(Long filmId) {
        return jdbcTemplate.query(
                "SELECT * FROM film_genres WHERE film_id = " + filmId,
                GenreDbStorage::resultSetToFilmGenre
        );
    }

    @Override
    public List<Genre> getGenres() {
        return jdbcTemplate.query(GET_ALL_QUERY, GenreDbStorage::resultSetToGenre);
    }

    private static Genre resultSetToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("name"))
                .build();
    }

    private static FilmGenre resultSetToFilmGenre(ResultSet rs, int rowNum) throws SQLException {
        return FilmGenre.builder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getLong("genre_id"))
                .build();
    }

}
