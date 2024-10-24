package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.director.Director;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_DIRECTOR = "INSERT INTO directors (name) VALUES (?)";
    private static final String UPDATE_DIRECTOR = "UPDATE directors SET name = ? WHERE director_id = ?";
    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE director_id = ?";
    private static final String SELECT_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE director_id = ?";
    private static final String SELECT_ALL_DIRECTORS = "SELECT * FROM directors";
    private static final String INSERT_FILM_DIRECTOR = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
    private static final String DELETE_FILM_DIRECTORS_BY_FILM_ID = "DELETE FROM film_directors WHERE film_id = ?";
    private static final String SELECT_DIRECTORS_BY_FILM_ID = "SELECT d.* FROM directors d JOIN film_directors fd ON d.director_id = fd.director_id WHERE fd.film_id = ?";
    private static final String SELECT_FILM_IDS_BY_DIRECTOR_ID = "SELECT film_id FROM film_directors WHERE director_id = ?";

    @Override
    public Director createDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(INSERT_DIRECTOR,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        director.setId(id);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        int rowsAffected = jdbcTemplate.update(UPDATE_DIRECTOR, director.getName(),
                director.getId());
        if (rowsAffected == 0) {
            throw new NotFoundException("Режиссёр с ID=" + director.getId() + " не найден");
        }
        return director;
    }

    @Override
    public void deleteDirector(Long directorId) {
        int rowsAffected = jdbcTemplate.update(DELETE_DIRECTOR, directorId);
        if (rowsAffected == 0) {
            throw new NotFoundException("Режиссёр с ID=" + directorId + " не найден");
        }
    }

    @Override
    public Director getDirectorById(Long directorId) {
        List<Director> directors = jdbcTemplate.query(SELECT_DIRECTOR_BY_ID, this::mapRowToDirector,
                directorId);
        if (directors.isEmpty()) {
            throw new NotFoundException("Режиссёр с ID=" + directorId + " не найден");
        }
        return directors.getFirst();
    }

    @Override
    public Collection<Director> getAllDirectors() {
        return jdbcTemplate.query(SELECT_ALL_DIRECTORS, this::mapRowToDirector);
    }

    @Override
    public void addDirectorsToFilm(Long filmId, List<Long> directorIds) {
        for (Long directorId : directorIds) {
            jdbcTemplate.update(INSERT_FILM_DIRECTOR, filmId, directorId);
        }
    }

    @Override
    public void removeDirectorsFromFilm(Long filmId) {
        jdbcTemplate.update(DELETE_FILM_DIRECTORS_BY_FILM_ID, filmId);
    }

    @Override
    public List<Director> getDirectorsByFilmId(Long filmId) {
        return jdbcTemplate.query(SELECT_DIRECTORS_BY_FILM_ID, this::mapRowToDirector, filmId);
    }

    @Override
    public List<Long> getFilmIdsByDirectorId(Long directorId) {
        return jdbcTemplate.queryForList(SELECT_FILM_IDS_BY_DIRECTOR_ID, Long.class, directorId);
    }

    private Director mapRowToDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("name"))
                .build();
    }
}
