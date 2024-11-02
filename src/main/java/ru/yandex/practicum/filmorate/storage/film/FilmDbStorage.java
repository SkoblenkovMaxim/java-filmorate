package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.rating.Rating;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

    @SuppressWarnings("all")
    private static final String GET_FILMS_BY_DIRECTOR_QUERY = """
            SELECT f.*
            FROM films f
            JOIN film_directors fd ON f.film_id = fd.film_id
            WHERE fd.director_id = ?
            """;

    @SuppressWarnings("all")
    private static final String FIND_FILMS_BY_USER_LIKES = """
            SELECT f.film_id
            FROM films f
            LEFT JOIN film_likes fl ON f.film_id = fl.film_id
            WHERE fl.user_id = ?
            """;

    @SuppressWarnings("all")
    private static final String FIND_RECOMMEND_FILMS_BY_USER_ID = """
            SELECT f.film_id
            FROM films f
            LEFT JOIN film_likes fl3 ON f.film_id = fl3.film_id
            WHERE fl3.user_id IN (SELECT fl2.user_id
                                 FROM film_likes AS fl2
                                 WHERE fl2.user_id != ?
                                 AND fl2.film_id IN (SELECT fl1.film_id
                                                       FROM film_likes AS fl1
                                                       WHERE fl1.user_id = ?)
                                 GROUP BY fl2.user_id
                                 ORDER BY COUNT(fl2.film_id) DESC
                LIMIT 1
                )
            """;

    private static final String GET_POPULAR_FILMS_QUERY = """
            SELECT f.*, COUNT(fl.like_id) AS likes
            FROM films f
            LEFT JOIN film_likes fl ON f.film_id = fl.film_id
            LEFT JOIN film_genres fg ON f.film_id = fg.film_id
            WHERE (fg.genre_id = ? OR ? IS NULL)
                AND (YEAR(f.release_date) = ? OR ? IS NULL)
            GROUP BY f.film_id
            ORDER BY likes DESC
            LIMIT ?
            """;

    @SuppressWarnings("all")
    private static final String FIND_COMMON_FILMS = """
            SELECT f.*
            FROM films f
            WHERE f.film_id IN (
                SELECT fl1.film_id
                FROM film_likes fl1
                WHERE fl1.user_id = ?
                INTERSECT
                SELECT fl2.film_id
                FROM film_likes fl2
                WHERE fl2.user_id = ?
            )
            ORDER BY (
                SELECT COUNT(*)
                FROM film_likes fl
                WHERE fl.film_id = f.film_id
            ) DESC;
            """;

    @SuppressWarnings("all")
    private static final String SEARCH_FILM_QUERY = """
            SELECT DISTINCT films.*, L.cnt FROM films
            LEFT JOIN film_directors ON films.film_id = film_directors.film_id
            LEFT JOIN directors ON film_directors.director_id = directors.director_id
            LEFT JOIN (SELECT film_id,count(DISTINCT user_id) cnt
            FROM film_likes GROUP BY film_id) AS L ON films.film_id = L.film_id
            """;


    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    @Override
    public Film saveFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_QUERY,
                    new String[]{"film_id"});
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
        // 2. обновить жанры
        // Удаляем существующие жанры фильма
        String deleteGenresQuery = """
                DELETE FROM film_genres
                WHERE film_id = ?;
                """;
        jdbcTemplate.update(deleteGenresQuery, film.getId());
        // Добавляем новые (обновленные) жанры фильма в БД
        String insertGenresQuery = """
                INSERT INTO film_genres (film_id, genre_id)
                VALUES (?, ?);
                """;
        if (film.getGenres() != null)
            film.getGenres().forEach(g -> jdbcTemplate.update(insertGenresQuery, film.getId(), g.getId()));
        return film;
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

    @Override
    public List<Film> getFilmsByDirector(Long directorId) {
        return jdbcTemplate.query(GET_FILMS_BY_DIRECTOR_QUERY, FilmDbStorage::mapRow, directorId);
    }

    @Override
    public List<Long> getFilmsLikesByUser(Long userId) {
        return jdbcTemplate.queryForList(FIND_FILMS_BY_USER_LIKES,
                Long.class,
                userId);
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) == null || userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("User or friend not found");
        }
        return jdbcTemplate.query(FIND_COMMON_FILMS, FilmDbStorage::mapRow, userId, friendId);
    }

    @Override
    public List<Long> getUsersRecommendations(Long userId) {
        return jdbcTemplate.queryForList(FIND_RECOMMEND_FILMS_BY_USER_ID,
                Long.class,
                userId, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer limit, Integer genreId, Integer year) {
        Object[] params = {
                genreId, genreId,
                year, year,
                limit
        };

        return jdbcTemplate.query(GET_POPULAR_FILMS_QUERY, FilmDbStorage::mapRow, params);
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

    @Override
    public List<Film> searchFilms(String query, String by) {
        String modQuery = " WHERE ";
        String param = "%" + query.toLowerCase() + "%";
        List<String> paramQuery = new ArrayList<>();

        if (by.toLowerCase().contains("director")) {
            modQuery += "lower(directors.name) like ?";
            paramQuery.add(param);
        }

        if (by.toLowerCase().contains("title")) {
            if (by.toLowerCase().contains("director")) {
                modQuery += " OR ";
            }
            modQuery += "lower(films.name) like ?";
            paramQuery.add(param);
        }

        modQuery += " ORDER BY L.cnt DESC ";

        return jdbcTemplate.query(
                SEARCH_FILM_QUERY + modQuery,
                FilmDbStorage::mapRow, paramQuery.toArray());
    }

}