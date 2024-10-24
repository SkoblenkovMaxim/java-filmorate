package ru.yandex.practicum.filmorate.storage.film;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.rating.Rating;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    @SuppressWarnings("all")
    private static final String CREATE_QUERY = """
            INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)
            """;

    @SuppressWarnings("all")
    private static final String GET_ALL_FILMS_QUERY = """
            SELECT * FROM films
            """;

    @SuppressWarnings("all")
    private static final String GET_FILM_BY_ID_QUERY = """
            SELECT * FROM films WHERE film_id = ?
            """;

    @SuppressWarnings("all")
    private static final String DELETE_FILM_BY_ID_QUERY = """
            DELETE FROM films WHERE film_id = ?
            """;

    @SuppressWarnings("all")
    private static final String UPDATE_FILM_QUERY = """
            UPDATE films
            SET name = ?,
                description = ?,
                release_date = ?,
                duration = ?,
                rating_id = ?
            WHERE film_id = ?
            """;

    //+BZ
    private final String SEARCH_FILM_QUERY = "SELECT DISTINCT films.*, L.cnt FROM films " +
            "LEFT JOIN film_directors ON films.film_id = film_directors.film_id " +
            "LEFT JOIN directors ON film_directors.director_id = directors.director_id " +
            "INNER JOIN (SELECT film_id,count(DISTINCT user_id) cnt FROM film_likes GROUP BY film_id) AS L ON films.film_id = L.film_id ";


    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film saveFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_QUERY, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5,
                    film.getMpa() != null
                            && film.getMpa().getId() != null
                            ? film.getMpa().getId()
                            : 0
            );
            return ps;
        }, keyHolder);

        var key = Objects.requireNonNull(keyHolder.getKey()).longValue();

        return Film.builder()
                .id(key)
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .mpa(film.getMpa())
                .genres(film.getGenres())
                .build();
    }

    @Override
    public void removeFilm(Long filmId) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(DELETE_FILM_BY_ID_QUERY);
            ps.setLong(1, filmId);
            return ps;
        });
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(
                UPDATE_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .build();
    }

    @Override
    public Film getFilm(Long filmId) {
        return jdbcTemplate.query(
                GET_FILM_BY_ID_QUERY,
                rs -> rs.next() ? mapRow(rs, 1) : null,
                filmId
        );
    }

    @Override
    public Collection<Film> getFilms() {
        return jdbcTemplate.query(
                GET_ALL_FILMS_QUERY,
                FilmDbStorage::mapRow
        );
    }

    private static Film mapRow(ResultSet rs, int i) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(Rating.builder().id(rs.getInt("rating_id")).build())
                .build();
    }

    //GET /films/search?query=крад&by=director,title
    //+BZ
    @Override
    public List<Film> getSearch(String query, String by) {
        String modQuery = " WHERE ";

        int countParam = 0;
        if (by.toLowerCase().contains("director")) {
            modQuery += "lower(directors.name) like '%" + query.toLowerCase() + "%'";
            countParam += 1;
        }

        if (by.toLowerCase().contains("title")) {
            if (countParam > 0) {
                modQuery += " OR ";
            }
            modQuery += "lower(films.name) like '%" + query.toLowerCase() + "%'";
            countParam += 1;
        }

        modQuery += " ORDER BY L.cnt DESC ";

        List<Film> films = jdbcTemplate.query(
                SEARCH_FILM_QUERY + modQuery,
                FilmDbStorage::mapRow
        );
        return films;
    }

}
