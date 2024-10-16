package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.like.Like;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;
    private final GenreStorage genreStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("likeDbStorage") LikeStorage likeStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage
    ) {
        this.filmStorage = filmStorage;
        this.likeStorage = likeStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
    }

    // добавление лайка
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film != null) {
            if (userStorage.getUserById(userId) != null) {
                likeStorage.createLike(Like.builder()
                        .filmId(filmId)
                        .userId(userId)
                        .build()
                );
            } else {
                throw new NotFoundException("Пользователь c ID=" + userId + " не найден!");
            }
        } else {
            throw new NotFoundException("Фильм c ID=" + filmId + " не найден!");
        }
    }

    // удаление лайка
    public void removeLike(Long filmId, Long userId) {
        likeStorage.removeLike(filmId, userId);
    }

    // вывод 10 наиболее популярных фильмов по количеству лайков
    public List<Film> getPopular(Integer count) {
        if (count < 1) {
            throw new ValidationException("Количество фильмов для вывода не должно быть меньше 1");
        }
        return likeStorage
                .getAllLikes()
                .stream()
                .map(like -> filmStorage.getFilm(like.getFilmId()))
                .collect(Collectors.toList());
    }

    public Film saveFilm(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза фильма не должна быть ранее 28 декабря 1895 год");
        }

        Film savedFilm = filmStorage.saveFilm(film);

        film.getGenres().forEach(genre ->
                genreStorage.createGenre(Genre.builder()
                        .filmId(savedFilm.getId())
                        .genreId(genre.getId())
                        .build()
                )
        );
        savedFilm.setGenres(film.getGenres());
        return savedFilm;
    }

    public void removeFilm(Long filmId) {
        filmStorage.removeFilm(filmId);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilm(Long filmId) {
        return filmStorage.getFilm(filmId);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }
}
