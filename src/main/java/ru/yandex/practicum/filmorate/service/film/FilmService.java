package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final LikeStorage likeStorage;

    // добавление лайка
    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film != null) {
            if (userStorage.getUserById(userId) != null) {
                likeStorage.addLike(filmId, userId);
                film.getLikes().add(userId);
            } else {
                throw new NotFoundException("Пользователь c ID=" + userId + " не найден!");
            }
        } else {
            throw new NotFoundException("Фильм c ID=" + filmId + " не найден!");
        }
    }

    // удаление лайка
    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.getFilm(filmId);
        if (film != null) {
            if (film.getLikes() != null && film.getLikes().contains(userId)) {
                likeStorage.deleteLike(filmId, userId);
            } else {
                throw new NotFoundException("Лайк от пользователя c ID=" + userId + " не найден!");
            }
        } else {
            throw new NotFoundException("Фильм c ID=" + filmId + " не найден!");
        }
    }

    // вывод 10 наиболее популярных фильмов по количеству лайков
    public List<Film> getPopular(Integer count) {
        if (count < 1) {
            throw new ValidationException("Количество фильмов для вывода не должно быть меньше 1");
        }
        return likeStorage.getPopular(count);
    }

    public Film saveFilm(Film newFilm) {
        return filmStorage.saveFilm(newFilm);
    }

    public Film removeFilm(Film film) {
        return filmStorage.removeFilm(film);
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
